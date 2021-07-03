package com.cla.home.ui

import android.os.Bundle
import android.view.View
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
import com.cla.wan.net.fail
import com.cla.wan.net.success
import com.cla.wan.utils.adapter.decoration.SpaceItemDecoration
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.app.createVm
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.youth.banner.Banner
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch


@Route(path = HomePath.HOME_FRAGMENT)
class HomeFragment : LateInitFragment() {

    companion object {
        private const val REFRESH_DATA = "refresh_data"
    }

    private val homeVm by lazy { createVm<HomeVm>() }
    private val banner by lazy { rootView.findViewById<Banner<HomeBannerBean, HomeBannerAdapter>>(R.id.banner) }
    private val bannerAdapter by lazy { HomeBannerAdapter(requireContext(), emptyList()) }

    private val homeAdapter by lazy {
        HomeArticleAdapter(requireContext(), this).apply {
            preloadEnable = true
            onPreload = { loadArticle() }
            clickArticle = { data ->
                ARouterUtil.navigation(WebPath.WEB_ACTIVITY) {
                    withParcelable(BaseConfig.WEB_PARAMS_KEY, WebParams(data.link, data.title))
                }
            }

            rvData.layoutManager = LinearLayoutManager(requireContext())
            rvData.addItemDecoration(SpaceItemDecoration(0, 15, 15, 15))
            rvData.adapter = this
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeVm.loadArticle.observe(viewLifecycleOwner, {
            println("lwl HomeFragment.loadData resource state=${it.state}")
            it.success {
                showContent()
                val end = it.data?.over ?: false
                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore(0, true, end)

                homeAdapter.loadSuccess(homeVm.nextPage, end)
                homeAdapter.addData(it?.data?.datas ?: emptyList())
            }.fail {
                if (rootView.contentViewShowed) {
                    homeAdapter.loadFailed()
                    refreshLayout.finishLoadMore(false)
                }
            }
        })

        homeVm.refreshPage.observe(viewLifecycleOwner, {

            it.success {
                val pageBean = it.data
                if (pageBean.isNullOrEmpty()) {
                    showEmpty()
                    return@observe
                }

                showContent()
                refreshLayout.finishRefresh()
                refreshLayout.finishLoadMore()

                val list = mutableListOf<HomeArticleData>()
                list.addAll(pageBean!!.topData)
                list.addAll(pageBean.pageData)
                homeAdapter.refreshData(list)
                bannerAdapter.setDatas(pageBean.bannerBean)
                banner.setCurrentItem(1, false)     //1这才是第一页
            }.fail {
                showError()

                if (rootView.contentViewShowed) {
                    refreshLayout.finishRefresh()
                    refreshLayout.finishLoadMore()
                }
            }
        })
    }

    override fun loadData() {
        println("lwl HomeFragment.loadData fragmentId=${fragmentId}")
        launch(REFRESH_DATA) { refreshPage() }
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
        println("lwl HomeFragment.loadArticle nextPage=${homeVm.nextPage}")
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
