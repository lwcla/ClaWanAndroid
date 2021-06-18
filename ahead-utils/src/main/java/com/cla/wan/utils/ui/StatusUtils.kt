package com.cla.wan.utils.ui

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import com.cla.wan.utils.R
import com.cla.wan.utils.app.AppUtils.getScreenWidth
import com.cla.wan.utils.app.colorValue
import com.cla.wan.utils.app.dp2px
import com.cla.wan.utils.widget.AlphaImageButton
import com.cla.wan.utils.widget.AlphaTextView
import com.cla.wan.utils.widget.CustomTitleBar
import com.cla.wan.utils.widget.TitleBarWrapper
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

fun Context.getShowText(res: Int? = -1): String? = try {
    getString(res ?: -1)
} catch (e: Exception) {
    null
}

inline fun FragmentActivity.initTitleBar(
    titleBar: CustomTitleBar,
    builder: TitleBarBuilder.() -> Unit = { }
): TitleBarWrapper = titleBar.run {

    with(TitleBarBuilder()) {
        builder(this)
        //设置标题栏中间的文字
        val centerTextView = TextView(this@initTitleBar)
        centerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.toFloat())
        centerTextView.typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗

        getShowText(centerTextRes).let {
            if (it.isNullOrBlank()) {
                centerTextView.text = ""
                centerTextView.visibility = View.GONE
            } else {
                centerTextView.text = it
                centerTextView.visibility = View.VISIBLE
            }
        }

        //如果文字太多的话，就隐藏一部分中间的字
        centerTextView.maxWidth = getScreenWidth() / 2
        centerTextView.ellipsize = TextUtils.TruncateAt.END
        centerTextView.maxLines = 1

        centerTextView.setTextColor(colorValue(centerTextColor))
        centerTextView.gravity = Gravity.CENTER
        rlCenter.removeAllViews()
        val cusParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        cusParams.addRule(RelativeLayout.CENTER_VERTICAL)
        rlCenter.addView(centerTextView, cusParams)

        //设置标题栏左边的返回图标
        val leftImageView = AlphaImageButton(this@initTitleBar)
        leftImageView.scaleType = ImageView.ScaleType.FIT_XY
        leftImageView.setImageDrawable(
            R.drawable.svg_navigation.toDrawable(this@initTitleBar, backImageColor)
        )
        leftImageView.setBackgroundResource(R.color.transparent)
        leftImageView.setOnClickListener { finish() }
        //左右的padding都设置，增加点击事件能够响应的范围
        val padding = 15.dp2px()
        leftImageView.setPadding(padding, padding, padding, padding)
        rlLeft.removeAllViews()
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_VERTICAL)
        rlLeft.addView(leftImageView, params)

        //设置标题栏右边的文字
        val rightTextView = AlphaTextView(this@initTitleBar)
        rightTextView.setTextColor(colorValue(rightTextColor))

        getShowText(rightTextRes).let {
            if (it.isNullOrBlank()) {
                rightTextView.text = ""
                rightTextView.visibility = View.GONE
            } else {
                rightTextView.text = it
                rightTextView.visibility = View.VISIBLE
            }
        }

        //左右的padding都设置，增加点击事件能够响应的范围
        rightTextView.setPadding(20.dp2px(), 0, 15.dp2px(), 0)
        rightTextView.setOnClickListener {
            rightTextClick.invoke(it)
        }
        rlRight.removeAllViews()
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        rlRight.addView(rightTextView, layoutParams)

        //设置标题栏和状态栏的颜色
        try {
            setBackgroundColor(colorValue(bgColorRes))
            statusColor(colorValue(bgColorRes), light = isStatusFontDark, fitWindow = false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        TitleBarWrapper(leftImageView, centerTextView, rightTextView, titleBar)
    }
}

typealias TitleRightClick = (View) -> Unit

class TitleBarBuilder(
    @Nullable @StringRes var centerTextRes: Int? = null,
    var centerText: String? = null,
    @Nullable @ColorRes var centerTextColor: Int = R.color.black,
    @ColorRes var backImageColor: Int = R.color.black,
    @Nullable @StringRes var rightTextRes: Int? = null,
    @ColorRes var rightTextColor: Int = R.color.black,
    @ColorRes var bgColorRes: Int = R.color.white,
    var isStatusFontDark: Boolean = true,
    var rightTextClick: TitleRightClick = {}
)
