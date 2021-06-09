package com.cla.home.vm

import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomeBannerBean
import com.cla.home.service.HomeService
import com.cla.wan.base.bean.BaseListData
import com.cla.wan.base.utils.fireBase

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
    fun loadHomeTopArticle() = fireBase<HomeService, BaseListData<HomeArticleData>> {
        loadHomeTopArticle()
    }

    /**
     *  置顶文章
     */
    fun loadBanner() = fireBase<HomeService, List<HomeBannerBean>>(forceCache = false) {
        loadBanner()
    }

}