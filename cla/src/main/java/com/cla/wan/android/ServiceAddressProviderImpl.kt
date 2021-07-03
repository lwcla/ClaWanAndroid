package com.cla.wan.android

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.wan.net.config.AddressType
import com.cla.wan.net.config.IServiceAddressProvider
import com.cla.wan.net.config.RouterHostPath
import com.cla.wan.net.config.ServiceAddressEntity
import com.cla.wan.net.utils.ServiceAddressUtil
import com.cla.wan.utils.app.AppUtils

@Route(path = RouterHostPath.SERVICE_ADDRESS_PROVIDER_IMPL)
class ServiceAddressProviderImpl : IServiceAddressProvider {

    companion object {
        private val debug = AppUtils.isDebug()

        private val addressList =
            listOf(
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.DEBUG, debug),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.TEST, false),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.PREVIEW, false),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.PRODUCTION, !debug),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.TEST, false)
            )

        private val DEFAULT_URL = addressList[0].address
    }

    override fun baseUrl(): String = addressList.find { it.active }?.address ?: DEFAULT_URL

    override fun getServiceAddress(): List<ServiceAddressEntity> = addressList

    override fun getActiveUrl(): String = ServiceAddressUtil.getActiveUrl()

    override fun init(context: Context?) {

    }
}