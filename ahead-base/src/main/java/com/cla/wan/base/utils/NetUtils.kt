package com.cla.wan.base.utils

import com.cla.wan.base.bean.BaseData
import com.cla.wan.net.fire
import retrofit2.Call

inline fun <reified S, reified T : Any> fireBase(
    forceCache: Boolean = false,
    cache: Boolean = true,
    baseUrl: String = "",
    crossinline block: suspend S.() -> Call<BaseData<T>>
) = fire(forceCache, cache, baseUrl, block)
