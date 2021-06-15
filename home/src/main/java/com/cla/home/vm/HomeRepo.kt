package com.cla.home.vm

import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomePageBean
import com.cla.home.service.HomeService
import com.cla.wan.base.bean.BaseListData
import com.cla.wan.base.utils.fireBase
import com.cla.wan.utils.net.CallResult
import com.cla.wan.utils.net.callAwait
import com.cla.wan.utils.net.fetch
import com.cla.wan.utils.net.fire
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class HomeRepository {

    /**
     *  首页文章列表
     */
    fun loadHomeData(page: Int) = fireBase<HomeService, BaseListData<HomeArticleData>> {
        loadHomeData(page)
    }

    /**
     *  首页文章详情
     */
    fun loadHomeArticleDetail(id: Int) = fire<HomeService, String> { loadHomeArticleDetail(id) }

    /**
     * 刷新
     */
    fun refreshData() = fetch<HomeService, HomePageBean>(forceCache = true) {
        withContext(Dispatchers.IO) {

            val bannerThread = async { loadBanner().callAwait() }
            val topThread = async { loadHomeTopArticle().callAwait() }
            val pageThread = async { loadHomeData(0).callAwait() }

            val bannerCall = bannerThread.await()
            val topCall = topThread.await()
            val pageCall = pageThread.await()

            CallResult.join(bannerCall, topCall, pageCall) {
                val banner = bannerCall.result?.data ?: emptyList()
                val top = topCall.result?.data ?: emptyList()
                val page = pageCall.result?.data?.datas ?: emptyList()
                top.forEach { it.isTop = true }
                HomePageBean(banner, top, page)
            }
        }
    }
}