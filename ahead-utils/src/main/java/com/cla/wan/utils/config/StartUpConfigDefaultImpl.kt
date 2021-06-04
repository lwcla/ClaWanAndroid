package com.cla.wan.utils.config

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = UtilsPath.START_UP_CONFIG_DEFAULT_IMPL)
class StartUpConfigDefaultImpl : StartUpConfig() {

    override fun init(context: Context?) {

    }
}