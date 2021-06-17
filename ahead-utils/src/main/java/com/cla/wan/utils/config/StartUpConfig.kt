package com.cla.wan.utils.config

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.template.IProvider
import com.cla.wan.utils.app.ARouterUtil

/**
 * 项目启动相关的配置
 */
abstract class StartUpConfig : IProvider {
    companion object {
        val impl by lazy {
            ARouterUtil.find<StartUpConfig>(
                HostPath.START_UP_CONFIG_IMPL,
                UtilsPath.START_UP_CONFIG_DEFAULT_IMPL
            )
        }
    }
}


@Route(path = UtilsPath.START_UP_CONFIG_DEFAULT_IMPL)
class StartUpConfigDefaultImpl : StartUpConfig() {

    override fun init(context: Context?) {

    }
}