package com.cla.home.vm

import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomePageBean
import com.cla.home.service.HomeService
import com.cla.wan.base.bean.BaseListData
import com.cla.wan.base.net.covert
import com.cla.wan.base.net.requestBase
import com.cla.wan.net.CallResult
import com.cla.wan.net.RequestEt.callAwait
import com.cla.wan.net.RequestEt.request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class HomeRepository {

    /**
     *  首页文章列表
     */
    fun loadHomeData(
        page: Int,
        block: (BaseListData<HomeArticleData>) -> Unit
    ) = requestBase<HomeService, BaseListData<HomeArticleData>>(
        call = { loadHomeData(page) }
    ) {
        processBean = { this.apply { block(this) } }
    }

    /**
     * 刷新
     */
    fun refreshData() = request<HomeService, HomePageBean>(
        call = {
            withContext(Dispatchers.IO) {
                val bannerThread = async(Dispatchers.IO) { loadBanner().callAwait().covert() }
                val topThread = async(Dispatchers.IO) { loadHomeTopArticle().callAwait().covert() }
                val pageThread = async(Dispatchers.IO) { loadHomeData(0).callAwait().covert() }

                val bannerCall = bannerThread.await()
                val topCall = topThread.await()
                val pageCall = pageThread.await()

                CallResult.join(bannerCall, topCall, pageCall) {
                    val banner = bannerCall.result ?: emptyList()
                    val top = topCall.result ?: emptyList()
                    val page = pageCall.result?.datas ?: emptyList()
                    HomePageBean(banner, top, page)
                }
            }
        }) {
        processBean = {
            this.apply { topData.forEach { it.isTop = true } }
        }
    }
}