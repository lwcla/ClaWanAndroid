package com.cla.home

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.home.adapter.HomeArticleAdapter
import com.cla.home.bean.HomeArticleData
import com.cla.home.vm.HomeVm
import com.cla.wan.base.config.HomePath
import com.cla.wan.base.ui.fragment.LateInitFragment
import com.cla.wan.utils.adapter.decoration.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch

@Route(path = HomePath.HOME_FRAGMENT)
class HomeFragment : LateInitFragment() {

    private val homeVm by lazy { ViewModelProvider(this).get(HomeVm::class.java) }

    private val homeAdapter by lazy {
        HomeArticleAdapter(requireContext()).apply {
            rvData.layoutManager = LinearLayoutManager(requireContext())
            rvData.addItemDecoration(SpaceItemDecoration(0, 15, 15, 15))
            rvData.adapter = this
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun loadData() {

        homeVm.loadArticle.observe(this, {
            showContent()
            homeAdapter.refreshData(it?.data?.datas ?: emptyList())
        })

        homeVm.refreshPage.observe(this, {
            showContent()
            val list = mutableListOf<HomeArticleData>()
            list.addAll(it.topData)
            list.addAll(it.pageData)
            homeAdapter.refreshData(list)
        })

        lifecycleScope.launch {
//            homeVm.loadArticle()
//            homeVm.loadBanner().observe(this@HomeFragment, {
//                println("lwl HomeFragment.loadData banner state=${it.state} data=${it.data}")
//            })

            homeVm.refreshHomeData()
        }
    }

    override fun initView() {

    }
}
