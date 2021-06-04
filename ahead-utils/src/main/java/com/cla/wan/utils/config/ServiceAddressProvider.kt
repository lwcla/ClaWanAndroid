package com.cla.wan.utils.config

import com.alibaba.android.arouter.facade.template.IProvider

abstract class ServiceAddressProvider : IProvider {
    fun baseUrl(): String = ""
}

