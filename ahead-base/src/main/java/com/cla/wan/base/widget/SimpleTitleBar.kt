package com.cla.wan.base.widget

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cn.fhstc.utils.ui.StatusUtils.getStatusBarHeight
import com.cla.wan.base.R
import com.cla.wan.utils.app.AppUtils.dp2px
import com.cla.wan.utils.app.AppUtils.getScreenWidth
import com.cla.wan.utils.widget.AlphaImageButton
import kotlin.math.max

/**
 * 只有简单的标题和返回按钮的 标题栏
 */
class SimpleTitleBar(context: Context, attr: AttributeSet? = null) : RelativeLayout(context, attr) {

    private val topPadding by lazy { max(dp2px(40), getStatusBarHeight()) }
    private val bottomPadding by lazy { dp2px(15) }
    private val startPadding = dp2px(20)
    private val ivSize by lazy { dp2px(20) }

    var showTitle = true

    private val ivClose by lazy {
        AlphaImageButton(context).apply {

            val width = ivSize + startPadding + startPadding
            val height = ivSize + topPadding + bottomPadding
            val params = LayoutParams(width, height)
            this.layoutParams = params

            //增加点击范围
            setPadding(startPadding, topPadding, startPadding, bottomPadding)

            scaleType = ImageView.ScaleType.FIT_XY
            setImageResource(R.mipmap.navigation_back_black)

            isClickable = true
            isFocusable = true
            setOnClickListener {
                (context as? Activity?)?.finish()
            }
        }
    }

    private val tvTitle by lazy {
        TextView(context).apply {
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.addRule(CENTER_HORIZONTAL)
            this.layoutParams = params

            setPadding(0, topPadding, 0, bottomPadding)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.toFloat())
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)//加粗
            //如果文字太多的话，就隐藏一部分中间的字
            maxWidth = getScreenWidth() / 2
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }
    }

    init {
        this.isClickable = true
        this.isFocusable = true

        addView(ivClose)
        addView(tvTitle)

        if (!showTitle) {
            tvTitle.text = ""
        }

        this.setOnClickListener {
            //点击标题栏不要有什么反应
        }
    }

    fun setClose(drawable: Drawable?) {
        ivClose.setImageDrawable(drawable)
    }

    fun setTitle(title: String) {
        tvTitle.text = title

        if (!showTitle) {
            tvTitle.text = ""
        }
    }
}