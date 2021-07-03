package com.cla.wan.net

import android.webkit.WebSettings
import com.cla.wan.net.config.ITokenProviderHelper
import com.cla.wan.utils.LifeCycleInjector
import com.cla.wan.utils.config.ModuleInfoHelper
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author  zhouyufei
 * @date  2020/4/12 1:02 PM
 * @version 1.0
 *      添加公共请求头
 */
class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val verName = ModuleInfoHelper.readModuleInfo().verName

        val reqBuilder = originalRequest.newBuilder()
            .addHeader("token", ITokenProviderHelper.getToken())
            .addHeader("ws-client", ITokenProviderHelper.clientType())
            .header(
                "User-Agent",
                "${WebSettings.getDefaultUserAgent(LifeCycleInjector.appContext)} lzm-version/${verName}"
            )
            .header("zzstc-appVersion", verName)
            .header("zzstc-osType", "android")
            .header("systemVersion", android.os.Build.VERSION.RELEASE)
            .header("phoneModel", "${android.os.Build.BRAND} /${android.os.Build.MODEL}")
        return chain.proceed(reqBuilder.build())
    }

}