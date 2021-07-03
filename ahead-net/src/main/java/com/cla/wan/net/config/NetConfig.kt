package com.cla.wan.net.config

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.net.HeaderInterceptor
import com.cla.wan.net.config.RouterHostPath.NET_CONFIG_IMPL
import com.cla.wan.net.config.RouterNetPath.NET_CONFIG_DEFAULT_IMPL
import com.cla.wan.utils.app.ARouterUtil
import okhttp3.Interceptor

object RouterNetPath {
    /**
     * 网络配置接口默认实现类
     */
    const val NET_CONFIG_DEFAULT_IMPL = "/net/NetConfigDefaultImpl"

    /**
     * token接口默认实现类
     */
    const val TOKEN_SERVICE_DEFAULT_IMPL = "/base/ITokenProviderDefaultImpl"
}

/**
 * 主程序模块
 */
object RouterHostPath {
    /**
     * 网络配置相关
     */
    const val NET_CONFIG_IMPL = "/host/NetConfigImpl"

    /**
     * token相关接口
     */
    const val TOKEN_SERVICE = "/host/ITokenProviderImpl"

    /**
     * 服务器地址相关
     */
    const val SERVICE_ADDRESS_PROVIDER_IMPL = "/host/ServiceAddressProviderImpl"
}

interface NetConfig : IProvider {
    companion object {
        /**
         * 保存在本地的token
         */
        const val TOKEN_LOCAL_KEY = "token_local_key"
    }

    /**
     * 网络拦截器
     */
    open fun getHeaderInterceptor(): Interceptor {
        return HeaderInterceptor()
    }
}

object NetConfigHelper{

    private val impl by lazy {
        ARouterUtil.find<NetConfig>(NET_CONFIG_IMPL, NET_CONFIG_DEFAULT_IMPL)
    }

    /**
     * 网络拦截器
     */
    fun getHeaderInterceptor() = impl?.getHeaderInterceptor()
}

@Route(path = NET_CONFIG_DEFAULT_IMPL)
class NetConfigDefaultImpl : NetConfig {

    override fun init(context: Context?) {

    }
}