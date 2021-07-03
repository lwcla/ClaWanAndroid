package com.cla.wan.base.net

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.wan.net.config.ITokenProvider
import com.cla.wan.net.config.RouterNetPath


@Route(path = RouterNetPath.TOKEN_SERVICE_DEFAULT_IMPL)
class ITokenProviderDefaultImpl : ITokenProvider {

    override fun init(context: Context?) {
        println("lwl ITokenProviderDefaultImpl.init")
    }
}