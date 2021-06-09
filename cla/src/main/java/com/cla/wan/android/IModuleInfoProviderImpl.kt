package com.cla.wan.android

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.wan.utils.config.HostPath
import com.cla.wan.utils.config.IModuleInfoProvider
import com.cla.wan.utils.entity.ModuleInfo

@Route(path = HostPath.APP_VERSION_SERVICE)
class IModuleInfoProviderImpl : IModuleInfoProvider {

    override fun readModuleInfo(): ModuleInfo = ModuleInfo(
        BuildConfig.APPLICATION_ID,
        BuildConfig.VERSION_NAME,
        BuildConfig.BUILD_HASH,
        BuildConfig.MODULE_NAME,
        BuildConfig.IS_DEBUG,
        BuildConfig.BUILD_TYPE
    )

    override fun init(context: Context?) {
    }
}