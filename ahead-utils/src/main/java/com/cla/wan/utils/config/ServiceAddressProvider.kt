package com.cla.wan.utils.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil

interface ServiceAddressProvider : IProvider {
    fun baseUrl(): String
}

object ServiceAddressHelper {

    private val impl by lazy {
        try {
            ARouterUtil.navigation(HostPath.SERVICE_ADDRESS_PROVIDER_IMPL) as ServiceAddressProvider
        } catch (e: Exception) {
            throw RuntimeException("没有找到ServiceAddressProvider的实现类")
        }
    }

    fun baseUrl(): String = impl.baseUrl()
}

enum class AddressType {
    DEBUG, //开发坏境
    TEST,  //测试环境
    PREVIEW,//预发布
    PRODUCTION, //正式环境
    OTHER //其他
}

data class ServiceAddressEntity(val address: String, val type: AddressType, val active: Boolean)