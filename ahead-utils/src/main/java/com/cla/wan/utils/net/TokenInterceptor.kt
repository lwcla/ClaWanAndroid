package com.cla.wan.utils.net

import com.cla.wan.utils.config.ITokenProviderHelper
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.locks.ReentrantLock

/**
 * @author  zhouyufei
 * @date  2020/4/12 1:02 PM
 * @version 1.0
 * token失效拦截处理
 */
class TokenInterceptor : Interceptor {

    private val refreshingTokenLocke = ReentrantLock()

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalResponse = chain.proceed(originalRequest)
        val originalUrl = originalRequest.url().toString()

        for (url in ITokenProviderHelper.ignoreUrls()) {
            if (originalUrl.contains(url)) {
                return originalResponse
            }
        }

        if (originalResponse.code() == 401) {

            //如果当前token是空的话，那就不用去刷新token了
            val currentToken = ITokenProviderHelper.getToken()
            if (currentToken.isEmpty()) {
                return originalResponse
            }

            refreshingTokenLocke.lock()
            val token = ITokenProviderHelper.refreshToken()
            val newToken = token.second
            var message = token.third

            if (newToken.isNotEmpty()) {
                // 重新执行上次请求
                val newRequest = chain.request().newBuilder().header("token", newToken).build()
                originalResponse.body()!!.close()
                val newResp = chain.proceed(newRequest)
                refreshingTokenLocke.unlock()
                return newResp
            } else {

                if (message.isNullOrEmpty()) {
                    message = "鉴权信息已失效"
                }

//                Logger.e("刷新token失败，返回登录界面")
                EventBus.getDefault().post(RefreshTokenFailed(message))
                refreshingTokenLocke.unlock()
                throw TokenFailureException()
            }
        }
        return originalResponse
    }
}

class TokenFailureException : RuntimeException("刷新token失败，返回登录界面")