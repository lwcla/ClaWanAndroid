package com.cla.wan.utils.app

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils

object AppUtils {

    /**
     * 使用QMUIDisplayHelper
     * 把dp转换成px值
     */
    fun dp2px(dp: Int): Int {
        return SizeUtils.dp2px(dp.toFloat())
    }

    /**
     * 使用QMUIDisplayHelper
     * 把dp转换成px值
     */
    fun dp2px(dp: Float): Int {
        return SizeUtils.dp2px(dp)
    }

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
}

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
