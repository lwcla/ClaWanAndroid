package com.cla.home.ui

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.home.R
import com.cla.home.adapter.HomeArticleAdapter
import com.cla.home.adapter.HomeBannerAdapter
import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomeBannerBean
import com.cla.home.bean.isNullOrEmpty
import com.cla.home.vm.HomeVm
import com.cla.wan.base.bean.WebParams
import com.cla.wan.base.config.BaseConfig
import com.cla.wan.base.config.HomePath
import com.cla.wan.base.config.WebPath
import com.cla.wan.base.ui.fragment.LateInitFragment
import com.cla.wan.utils.adapter.decoration.SpaceItemDecoration
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.app.createVm
import com.cla.wan.utils.net.ResourceState
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.youth.banner.Banner
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch


@Route(path = HomePath.HOME_FRAGMENT)
class HomeFragment : LateInitFragment() {

    private val homeVm by lazy { createVm<HomeVm>() }
    private val banner by lazy { rootView.findViewById<Banner<HomeBannerBean, HomeBannerAdapter>>(R.id.banner) }
    private val bannerAdapter by lazy { HomeBannerAdapter(requireContext(), emptyList()) }

    private val homeAdapter by lazy {
        HomeArticleAdapter(requireContext(), this).apply {
            preloadEnable = true
            onPreload = { loadArticle() }
            clickArticle = { data ->
                ARouterUtil.navigation(WebPath.WEB_ACTIVITY) {
                    withParcelable(BaseConfig.WEB_PARAMS_KEY, WebParams(data.link))
                }
            }

            rvData.layoutManager = LinearLayoutManager(requireContext())
            rvData.addItemDecoration(SpaceItemDecoration(0, 15, 15, 15))
            rvData.adapter = this
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun loadData() {
        println("lwl HomeFragment.loadData fragmentId=${fragmentId}")
        homeVm.loadArticle.observe(this, {

            when (it.state) {
                ResourceState.Success -> {
                    showContent()
//                    val end = it.data?.over ?: false
                    val end = homeVm.nextPage >= 5
                    refreshLayout.finishLoadMore(0, true, end)

                    homeAdapter.loadSuccess(homeVm.nextPage, end)
                    homeAdapter.addData(it?.data?.datas ?: emptyList())
                }

                ResourceState.Failure -> {
                    homeAdapter.loadFailed()
                    refreshLayout.finishLoadMore(false)
                }
            }
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

                ResourceState.Failure -> {
                    showError()

                    if (rootView.contentViewShowed) {
                        refreshLayout.finishRefresh()
                        refreshLayout.finishLoadMore()
                    }
                }
            }
        })

        refreshPage()
    }

    override fun initView() {
        println("lwl HomeFragment.initView")
        refreshLayout.apply {
            setRefreshHeader(MaterialHeader(requireContext()))
            setRefreshFooter(ClassicsFooter(requireContext()))
            setEnableLoadMore(false)
            setOnRefreshListener { refreshPage() }
            setOnLoadMoreListener { loadArticle() }
        }

        banner.addBannerLifecycleObserver(this@HomeFragment)//添加生命周期观察者
        banner.setAdapter(bannerAdapter)
    }

    private fun loadArticle(): Int {
        homeVm.loadArticle()
        return homeVm.nextPage
    }

    private fun refreshPage() {
        lifecycleScope.launch { homeVm.refreshHomeData() }
    }

    override fun refresh() {
        println("lwl HomeFragment.refresh contentViewShowed=${rootView.contentViewShowed}")
        if (rootView.contentViewShowed) {
            refreshLayout.autoRefresh()
            rvData.scrollToPosition(0)
            val behavior = (appBarLayout.layoutParams as? CoordinatorLayout.LayoutParams?)?.behavior
            if (behavior is AppBarLayout.Behavior && behavior.topAndBottomOffset != 0) {
                behavior.topAndBottomOffset = 0
            }
        } else {
            refreshPage()
        }
    }
}