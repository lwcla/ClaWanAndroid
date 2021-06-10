package com.cla.home.vm

import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomeBannerBean
import com.cla.home.bean.HomePageBean
import com.cla.home.service.HomeService
import com.cla.wan.base.bean.BaseListData
import com.cla.wan.base.utils.fireBase
import com.cla.wan.utils.net.fetch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.await

class HomeRepository {

    /**
     *  首页文章列表
     */
    fun loadHomeData(page: Int) = fireBase<HomeService, BaseListData<HomeArticleData>> {
        loadHomeData(page)
    }

    /**
     *  置顶文章
     */
    fun loadHomeTopArticle() = fireBase<HomeService, List<HomeArticleData>> {
        loadHomeTopArticle()
    }

    /**
     *  置顶文章
     */
    fun loadBanner() = fireBase<HomeService, List<HomeBannerBean>>(forceCache = false) {
        loadBanner()
    }

    fun refreshData() = fetch<HomeService, HomePageBean>(forceCache = true) {

        withContext(Dispatchers.IO) {

            val bannerThread = async { loadBanner().await() }
            val topThread = async { loadHomeTopArticle().await() }
            val pageThread = async { loadHomeData(0).await() }

            val banner = bannerThread.await().data ?: emptyList()
            val top = topThread.await().data ?: emptyList()
            val page = pageThread.await().data?.datas ?: emptyList()

            HomePageBean(banner, top, page)
        }
    }

}