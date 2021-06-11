package com.cla.home.bean

/**
 * 首页文章列表
 */
data class HomeArticleData(
    val apkLink: String,
    val audit: Int,
    val author: String?,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String?,
    var collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean?,
    val host: String,
    val id: Int,
    val link: String,
    val niceDate: String?,
    val niceShareDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long?,
    val realSuperChapterId: Int,
    val selfVisible: Int,
    val shareDate: Long?,
    val shareUser: String?,
    val superChapterId: Int,
    val superChapterName: String?,
    val tags: List<HomeArticleTag>,
    val title: String?,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    var isTop: Boolean = false // 置顶
)

data class HomeArticleTag(
    val name: String,
    val url: String
)

internal fun HomeArticleData.owner() = if (!author.isNullOrBlank()) {
    "作者:$author"
} else if (!shareUser.isNullOrBlank()) {
    "分享人:$shareUser"
} else {
    ""
}

internal fun HomeArticleData.timeByNow(): String = niceDate ?: ""

/**
 * 分类
 */
internal fun HomeArticleData.classText() = StringBuilder().apply {

    append("分类:")

    if (!superChapterName.isNullOrBlank()) {
        append(superChapterName)
    }

    if (!chapterName.isNullOrBlank()) {
        append("/").append(chapterName)
    }
}