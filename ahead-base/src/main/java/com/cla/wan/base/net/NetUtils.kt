package com.cla.wan.base.net

import com.cla.wan.base.bean.BaseData
import com.cla.wan.net.*
import com.cla.wan.net.RequestEt.callAwait
import retrofit2.Call


/**
 * 请求成功
 */
fun <T> CallResult<BaseData<T>>.baseSuc() = suc() && result?.run { data != null } ?: false

fun <T> CallResult<BaseData<T>>.toBaseRes(covert: (T) -> T = { it }) = this.result.run {

    val errCode = this?.errorCode
    val errMsg = this?.errorMsg

    val code = if (errCode == null || errCode <= 0) {
        httpCode
    } else {
        errCode
    }
    val msg = if (errMsg.isNullOrBlank()) {
        message
    } else {
        errMsg
    }

    if (baseSuc()) {
        val data = this?.data?.run { covert(this) }
        Resource.success<T>(data, code = code)
    } else {
        Resource.failure<T>(message = msg)
    }
}


fun <T> CallResult<BaseData<T>>.covert(covert: (T) -> T = { it }) = this.result.run {

    val errCode = this?.errorCode
    val errMsg = this?.errorMsg

    val code = if (errCode == null || errCode <= 0) {
        httpCode
    } else {
        errCode
    }
    val msg = if (errMsg.isNullOrBlank()) {
        message
    } else {
        errMsg
    }

    if (baseSuc()) {
        val data = this?.data?.run { covert(this) }
        CallResult(true, code, data, msg)
    } else {
        CallResult(false, code, null, msg)
    }
}


suspend inline fun <reified ResultType> Call<BaseData<ResultType>>.callBaseAwait() =
    callAwait().toBaseRes()

inline fun <reified S, reified T> requestBase(
    noinline call: suspend (S.() -> Call<BaseData<T>>),
    noinline builder: RequestBuilder<S, BaseData<T>, T>.() -> Unit = {}
) = NetUtils.requestBase(S::class.java, call = call, builder = builder)

object NetUtils {

    fun <S, T> requestBase(
        cls: Class<S>,
        call: suspend (S.() -> Call<BaseData<T>>),
        builder: RequestBuilder<S, BaseData<T>, T>.() -> Unit = {}
    ) = RequestEt.request(
        createService = { type, baseUrl -> RetrofitManager.create(cls, type, baseUrl) },
        call = { call(this).callAwait() },
        mapResult = { params -> toBaseRes(params.processBean) },
        builder = builder
    )
}