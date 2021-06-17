package com.cla.wan.utils.widget

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.cla.wan.utils.R
import com.cla.wan.utils.app.AppUtils.getScreenWidth
import com.cla.wan.utils.app.colorValue
import com.cla.wan.utils.app.dp2px
import com.cla.wan.utils.ui.StatusUtils.getStatusBarHeight
import com.cla.wan.utils.ui.statusColor
import com.cla.wan.utils.ui.toDrawable
import kotlinx.android.synthetic.main.layout_custom_top_bar.view.*

inline fun FragmentActivity.initBar(
    titleBar: CustomTitleBar,
    @Nullable @StringRes centerTextRes: Int? = null,
    @Nullable @ColorRes centerTextColor: Int = R.color.color_333333,
    @ColorRes backImageColor: Int = R.color.color_333333,
    @Nullable @StringRes rightTextRes: Int? = null,
    @ColorRes rightTextColor: Int = R.color.color_333333,
    @ColorRes bgColorRes: Int = R.color.white,
    isStatusFontDark: Boolean = true,
    crossinline rightTextClick: (view: View) -> Unit = {}
): TitleBarWrapper = titleBar.run {

    //设置标题栏中间的文字
    val centerText = TextView(this@initBar)
    centerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.toFloat())
    centerText.typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
    if (centerTextRes != null) {
        centerText.text = getString(centerTextRes)
        centerText.visibility = View.VISIBLE
    } else {
        centerText.text = ""
        centerText.visibility = View.GONE
    }
    //如果文字太多的话，就隐藏一部分中间的字
    centerText.maxWidth = getScreenWidth() / 2
    centerText.ellipsize = TextUtils.TruncateAt.END
    centerText.maxLines = 1

    centerText.setTextColor(colorValue(centerTextColor))
    centerText.gravity = Gravity.CENTER
    rlCenter.removeAllViews()
    val cusParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.MATCH_PARENT,
        RelativeLayout.LayoutParams.WRAP_CONTENT
    )
    cusParams.addRule(RelativeLayout.CENTER_VERTICAL)
    rlCenter.addView(centerText, cusParams)

    //设置标题栏左边的返回图标
    val leftImageView = AlphaImageButton(this@initBar)
    leftImageView.scaleType = ImageView.ScaleType.FIT_XY
    leftImageView.setImageDrawable(
        R.drawable.svg_navigation.toDrawable(this@initBar, backImageColor)
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
    val rightText = AlphaTextView(this@initBar)
    rightText.setTextColor(colorValue(rightTextColor))
    if (rightTextRes != null) {
        rightText.text = getString(rightTextRes)
        rightText.visibility = View.VISIBLE
    } else {
        rightText.text = ""
        rightText.visibility = View.GONE
    }
    //左右的padding都设置，增加点击事件能够响应的范围
    rightText.setPadding(20.dp2px(), 0, 15.dp2px(), 0)
    rightText.setOnClickListener {
        rightTextClick.invoke(it)
    }
    rlRight.removeAllViews()
    val layoutParams = RelativeLayout.LayoutParams(
        RelativeLayout.LayoutParams.WRAP_CONTENT,
        RelativeLayout.LayoutParams.MATCH_PARENT
    )
    layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
    rlRight.addView(rightText, layoutParams)

    //设置标题栏和状态栏的颜色
    try {
        setBackgroundColor(colorValue(bgColorRes))
        statusColor(colorValue(bgColorRes), light = isStatusFontDark, fitWindow = false)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return TitleBarWrapper(leftImageView, centerText, rightText, this)
}

/**
 * 标题栏
 */
class CustomTitleBar(context: Context, attr: AttributeSet? = null) :
    ConstraintLayout(context, attr) {

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.layout_custom_top_bar, this, true)
        setPadding(0, getStatusBarHeight(), 0, 0)
    }
}

class TitleBarWrapper(
    val leftImageView: ImageView,
    val centerTextView: TextView,
    val rightTextView: TextView,
    val titleBar: CustomTitleBar
) {

    /**
     * 重置标题栏中间的文字
     */
    fun resetCenterText(@StringRes stringRes: Int) {
        try {
            centerTextView.apply {
                setText(stringRes)
                visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 重置标题栏中间的文字
     */
    fun resetCenterText(string: String?) {
        try {
            centerTextView.apply {
                text = string
                visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

