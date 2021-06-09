package com.cla.wan.android

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.wan.utils.app.AppUtils
import com.cla.wan.utils.config.AddressType
import com.cla.wan.utils.config.HostPath
import com.cla.wan.utils.config.ServiceAddressEntity
import com.cla.wan.utils.config.ServiceAddressProvider

@Route(path = HostPath.SERVICE_ADDRESS_PROVIDER_IMPL)
class ServiceAddressProviderImpl : ServiceAddressProvider {

    companion object {
        private const val DEFAULT_URL = "https://www.wanandroid.com"
        private val debug = AppUtils.isDebug()

        private val addressList =
            listOf(
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.DEBUG, debug),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.TEST, false),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.PREVIEW, false),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.PRODUCTION, !debug),
                ServiceAddressEntity("https://www.wanandroid.com", AddressType.TEST, false)
            )
    }

    override fun baseUrl(): String = addressList.find { it.active }?.address ?: DEFAULT_URL

    override fun init(context: Context?) {

    }
}