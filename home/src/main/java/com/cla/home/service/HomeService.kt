package com.cla.home.service

import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.HomeBannerBean
import com.cla.home.config.ApiUrl
import com.cla.wan.base.bean.BaseData
import com.cla.wan.base.bean.BaseListData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeService {


    /**
     *  banner
     */
    @GET(ApiUrl.HOME_BANNER)
    fun loadBanner(): Call<BaseData<List<HomeBannerBean>>>

    /**
     *  置顶文章
     */
    @GET(ApiUrl.HOME_TOP_ARTICLE)
    fun loadHomeTopArticle(): Call<BaseData<List<HomeArticleData>>>

    /**
     *  首页文章列表
     */
    @GET(ApiUrl.HOME_ARTICLE)
    fun loadHomeData(@Path("page") page: Int): Call<BaseData<BaseListData<HomeArticleData>>>
}