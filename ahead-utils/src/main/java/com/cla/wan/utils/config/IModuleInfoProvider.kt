package com.cla.wan.utils.config

import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.entity.ModuleInfo

interface IModuleInfoProvider : IProvider {
    fun readModuleInfo(): ModuleInfo
}

object ModuleInfoHelper {
    val impl by lazy {
        try {
            ARouterUtil.navigation(HostPath.APP_VERSION_SERVICE) as IModuleInfoProvider
        } catch (e: Exception) {
            throw RuntimeException("没有找到IModuleInfoProvider的实现类")
        }
    }

    fun readModuleInfo(): ModuleInfo = impl.readModuleInfo()
}