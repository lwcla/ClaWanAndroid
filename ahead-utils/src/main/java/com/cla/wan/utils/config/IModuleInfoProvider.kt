package com.cla.wan.utils.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.entity.ModuleInfo

interface IModuleInfoProvider : IProvider {
    fun readModuleInfo(): ModuleInfo
}

object ModuleInfoHelper {
    val impl by lazy { ARouterUtil.navigation(UtilsPath.APP_VERSION_SERVICE) as? IModuleInfoProvider? }
    fun readModuleInfo(): ModuleInfo? = impl?.readModuleInfo()
}