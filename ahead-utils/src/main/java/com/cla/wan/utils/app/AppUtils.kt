package com.cla.wan.utils.app

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.cla.wan.utils.config.ModuleInfoHelper

object AppUtils {


    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth(): Int {
        return ScreenUtils.getScreenWidth()
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenHeight(): Int {
        return ScreenUtils.getScreenHeight()
    }

    /**
     * 是否debug模式
     */
    fun isDebug() = ModuleInfoHelper.impl.readModuleInfo().debug
}

/**
 * 使用QMUIDisplayHelper
 * 把dp转换成px值
 */
fun Int.dp2px() = SizeUtils.dp2px(this.toFloat())

/**
 * 使用QMUIDisplayHelper
 * 把dp转换成px值
 */
fun Float.dp2px(): Int = SizeUtils.dp2px(this)

/**
 * 关闭软件盘
 */
fun Context?.closeInput() {
    if (this == null) {
        return
    }

    val aty = this as? FragmentActivity? ?: return
    aty.closeInput()
}

/**
 * 关闭软件盘
 */
fun FragmentActivity.closeInput() {
    closeInput(window.peekDecorView())
}

/**
 * 关闭软件盘
 */
fun FragmentActivity.closeInput(view: View?) {

    if (view == null) {
        return
    }

    try {
        val inputManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
