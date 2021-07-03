package com.cla.wan.net.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil

interface IServiceAddressProvider : IProvider {
    fun baseUrl(): String

    fun getServiceAddress(): List<ServiceAddressEntity>

    fun getActiveUrl(): String
}

object ServiceAddressHelper {

    private val impl by lazy {
        ARouterUtil.find<IServiceAddressProvider>(RouterHostPath.SERVICE_ADDRESS_PROVIDER_IMPL)
    }

    fun getServiceAddress() = impl?.getServiceAddress() ?: emptyList()

    fun baseUrl() = impl?.getActiveUrl().run {
        if (isNullOrBlank()) {
            impl?.baseUrl() ?: ""
        } else {
            this
        }
    }
}

enum class AddressType {
    DEBUG, //开发坏境
    TEST,  //测试环境
    PREVIEW,//预发布
    PRODUCTION, //正式环境
    OTHER //其他
}

data class ServiceAddressEntity(val address: String, val type: AddressType, var active: Boolean)