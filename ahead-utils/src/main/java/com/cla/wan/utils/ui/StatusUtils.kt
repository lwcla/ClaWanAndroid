package com.cla.wan.utils.ui

import android.view.View
import androidx.annotation.ColorRes
import androidx.fragment.app.FragmentActivity
import com.zackratos.ultimatebarx.ultimatebarx.UltimateBarX
import com.zackratos.ultimatebarx.ultimatebarx.bean.BarBackground
import com.zackratos.ultimatebarx.ultimatebarx.bean.BarConfig

/**
 * 状态栏
 */
object StatusUtils {
    /**
     * 获取状态栏的高度
     */
    fun getStatusBarHeight(): Int {
        return UltimateBarX.getStatusBarHeight()
    }


    /**
     * 获取状态栏的高度
     */
    fun addStatusBarTopPadding(view: View) {
        UltimateBarX.addStatusBarTopPadding(view)
    }
}

/**
 * 设置状态栏颜色
 * @param statusColorRes 状态栏颜色
 * @param  light  状态栏字体 true: 灰色，false: 白色 Android 6.0+; 导航栏按钮 true: 灰色，false: 白色 Android 8.0+
 * @param fitWindow 布局是否侵入状态栏（true 不侵入，false 侵入）
 */
fun FragmentActivity.statusColor(
    @ColorRes statusColorRes: Int,
    light: Boolean = false,
    fitWindow: Boolean = true
) {

    val background = BarBackground.newInstance()    // 创建 background 对象
        .colorRes(statusColorRes)              // 状态栏/导航栏背景颜色（资源id）

    val config = BarConfig.newInstance()            // 创建配置对象
        .fitWindow(fitWindow)                   // 布局是否侵入状态栏（true 不侵入，false 侵入）
        .background(background)                     // 设置 background 对象
        .light(light)

    UltimateBarX.with(this)                  // 对当前 Activity 或 Fragment 生效
        .config(config)                             // 使用配置
        .applyStatusBar()                           // 应用到状态栏
}

