package com.cla.wan.base.bean


/**
 *
 * {
 *  "data": ...,
 *  "errorCode": 0,
 *  "errorMsg": ""
 * }
 */
data class BaseData<T>(val data: T?, val errorCode: Int = 0, val errorMsg: String? = "")

/**
 * {"curPage":1,"datas":[],"offset":0,"over":false,"pageCount":530,"size":20,"total":10589}
 */
data class BaseListData<T>(
    val curPage: Int,
    val datas: List<T>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Long
)