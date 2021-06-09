package com.cla.wan.utils.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.net.HeaderInterceptor
import okhttp3.Interceptor

/**
 * 项目启动相关的配置
 */
abstract class StartUpConfig : IProvider {
    companion object {
        val impl by lazy {
            try {
                ARouterUtil.navigation(HostPath.START_UP_CONFIG_IMPL) as? StartUpConfig?
            } catch (e: Exception) {
                null
            } ?: ARouterUtil.navigation(UtilsPath.START_UP_CONFIG_DEFAULT_IMPL) as StartUpConfig
        }
    }

    /**
     * 网络拦截器
     */
    open fun getHeaderInterceptor(): Interceptor {
        return HeaderInterceptor()
    }
}