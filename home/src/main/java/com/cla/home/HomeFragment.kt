package com.cla.home

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.home.adapter.HomeArticleAdapter
import com.cla.home.adapter.HomeBannerAdapter
import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomeBannerBean
import com.cla.home.bean.isNullOrEmpty
import com.cla.home.vm.HomeVm
import com.cla.wan.base.config.HomePath
import com.cla.wan.base.ui.fragment.LateInitFragment
import com.cla.wan.utils.adapter.decoration.SpaceItemDecoration
import com.cla.wan.utils.net.ResourceState
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.youth.banner.Banner
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch

@Route(path = HomePath.HOME_FRAGMENT)
class HomeFragment : LateInitFragment() {

    private val homeVm by lazy { ViewModelProvider(this).get(HomeVm::class.java) }
    private val banner by lazy { rootView.findViewById<Banner<HomeBannerBean, HomeBannerAdapter>>(R.id.banner) }
    private val bannerAdapter by lazy { HomeBannerAdapter(requireContext(), emptyList()) }

    private val homeAdapter by lazy {
        HomeArticleAdapter(requireContext(), this).apply {
            rvData.layoutManager = LinearLayoutManager(requireContext())
            rvData.addItemDecoration(SpaceItemDecoration(0, 15, 15, 15))
            rvData.adapter = this
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun loadData() {
        homeVm.loadArticle.observe(this, {
            showContent()
            refreshLayout.finishLoadMore()
            homeAdapter.addData(it?.data?.datas ?: emptyList())
        })

        homeVm.refreshPage.observe(this, {
            when (it.state) {
                ResourceState.Success -> {
                    val pageBean = it.data
                    if (pageBean.isNullOrEmpty()) {
                        showEmpty()
                        return@observe
                    }

                    showContent()
                    refreshLayout.finishRefresh()
                    val list = mutableListOf<HomeArticleData>()
                    list.addAll(pageBean!!.topData)
                    list.addAll(pageBean.pageData)
                    homeAdapter.refreshData(list)

                    bannerAdapter.setDatas(pageBean.bannerBean)
                    banner.setCurrentItem(1, false)     //1这才是第一页
                }

                ResourceState.Failure -> showError()
            }
        })

        lifecycleScope.launch {
            homeVm.refreshHomeData()
        }
    }

    override fun initView() {

        refreshLayout.setRefreshHeader(MaterialHeader(requireContext()))
        refreshLayout.setRefreshFooter(ClassicsFooter(requireContext()))
        refreshLayout.setOnRefreshListener { homeVm.refreshHomeData() }
        refreshLayout.setOnLoadMoreListener { homeVm.loadArticle() }

        banner.addBannerLifecycleObserver(this@HomeFragment)//添加生命周期观察者
        banner.setAdapter(bannerAdapter)

        homeAdapter.showArticleDetail = { bean ->



        }
    }
}
