package com.cla.wan.utils.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil


interface ITokenProvider : IProvider {

    fun getToken(): String

    /**
     * 刷新token
     * @return 后台返回的code  新的token  请求的错误信息
     */
    fun refreshToken(): Triple<Int, String, String?>

    fun ignoreUrls(): Array<String>
    fun clientType(): String

    /**
     * 保存token
     */
    fun saveToken(token: String)
}

object ITokenProviderHelper {

    val impl by lazy { ARouterUtil.navigation(HostPath.TOKEN_SERVICE) as? ITokenProvider? }

    fun getToken(): String = impl?.getToken() ?: ""


    /**
     * 刷新token
     * @return 后台返回的code  新的token  请求的错误信息
     */
    fun refreshToken(): Triple<Int, String, String?> = impl?.refreshToken() ?: Triple(0, "", "")

    fun ignoreUrls(): Array<String> = impl?.ignoreUrls() ?: arrayOf("")

    fun clientType(): String = impl?.clientType() ?: ""

    fun saveToken(token: String) = impl?.saveToken(token)
}