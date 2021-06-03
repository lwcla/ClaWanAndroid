package com.cla.wan.base.utils

import android.content.Context
import android.content.Intent
import com.alibaba.android.arouter.facade.Postcard
import com.cla.wan.base.config.BaseConfig
import com.cla.wan.base.config.ScaffoldPath
import com.cla.wan.utils.app.ARouterUtil

object Utils {

    //关闭所有Activity
    fun finishAllActivity(context: Context) {
        toMainAty(context) {
            withBoolean(BaseConfig.MAIN_PAGE_EXIT_APP_KEY, true)
        }
    }

    /**
     * 如果要启动MainActivity必须要使用这个方法
     * https://blog.csdn.net/ethanco/article/details/50128689
     */
    inline fun toMainAty(context: Context, block: Postcard.() -> Unit = {}) {
        ARouterUtil.navigation(context, ScaffoldPath.MAIN_ACTIVITY) {
            withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            block(this)
        }
    }

}