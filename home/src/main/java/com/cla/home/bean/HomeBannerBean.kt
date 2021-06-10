package com.cla.home.bean

/**
 * 首页banner
 */
data class HomeBannerBean(
    val desc: String,
    val id: Int,
    val imagePath: String?,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)

/**
 * 首页数据
 *
 * @param bannerBean banner
 * @param topData 置顶数据
 * @param pageData 首页数据
 */
data class HomePageBean(
    var bannerBean: List<HomeBannerBean> = emptyList(),
    var topData: List<HomeArticleData> = emptyList(),
    var pageData: List<HomeArticleData> = emptyList()
)

internal fun HomePageBean?.isNullOrEmpty() =
    this?.run { bannerBean.isEmpty() && topData.isEmpty() && pageData.isEmpty() } ?: true


