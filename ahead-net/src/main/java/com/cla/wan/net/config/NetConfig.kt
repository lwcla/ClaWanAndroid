package com.cla.wan.net.config

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.net.HeaderInterceptor
import com.cla.wan.utils.app.ARouterUtil
import okhttp3.Interceptor

object NetPath {
    const val NET_CONFIG_DEFAULT_IMPL = "/ahead_net/NetConfigDefaultImpl"
    const val NET_CONFIG_IMPL = "/host/NetConfigImpl"
}

interface NetConfig : IProvider {
    companion object {
        val impl by lazy {
            ARouterUtil.find<NetConfig>(
                NetPath.NET_CONFIG_IMPL,
                NetPath.NET_CONFIG_DEFAULT_IMPL
            )
        }
    }

    /**
     * 网络拦截器
     */
    open fun getHeaderInterceptor(): Interceptor {
        return HeaderInterceptor()
    }
}

@Route(path = NetPath.NET_CONFIG_DEFAULT_IMPL)
class NetConfigDefaultImpl : NetConfig {

    override fun init(context: Context?) {

    }
}