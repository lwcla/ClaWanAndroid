package com.cla.wan.net.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.net.config.NetConfig.Companion.TOKEN_LOCAL_KEY
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.data.loadData
import com.cla.wan.utils.data.removeValueForKey
import com.cla.wan.utils.data.saveData

interface ITokenProvider : IProvider {
    /**
     * 刷新token
     * @return 后台返回的code  新的token  请求的错误信息
     */
    fun refreshToken(): Triple<Int, String, String?> = Triple(200, "", null)

    fun ignoreUrls(): List<String> = emptyList()

    fun clientType(): String = "app"

    /**
     * 清除本地token
     */
    fun clearToken() = TOKEN_LOCAL_KEY.removeValueForKey()

    fun getToken(): String = TOKEN_LOCAL_KEY.loadData("")

    /**
     * 保存token
     */
    fun saveToken(token: String) = TOKEN_LOCAL_KEY.saveData(value = token)
}

object ITokenProviderHelper {

    val impl by lazy {
        ARouterUtil.find<ITokenProvider>(
            RouterHostPath.TOKEN_SERVICE,
            RouterNetPath.TOKEN_SERVICE_DEFAULT_IMPL
        )
    }

    /**
     * 获取token
     */
    fun getToken(): String = impl?.getToken() ?: ""


    /**
     * 刷新token
     * @return 后台返回的code  新的token  请求的错误信息
     */
    fun refreshToken(): Triple<Int, String, String?> = impl?.refreshToken() ?: Triple(0, "", "")

    fun ignoreUrls(): List<String> = impl?.ignoreUrls() ?: emptyList()

    /**
     * 网络请求中的header添加的 ws-client 的值
     */
    fun clientType(): String = impl?.clientType() ?: "app"

    /**
     * 保存token
     */
    fun saveToken(token: String) = impl?.saveToken(token)

    /**
     * 清除本地token
     */
    fun clearToken() = impl?.clearToken()
}