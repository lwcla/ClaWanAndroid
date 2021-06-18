package com.cla.wan.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.cla.wan.utils.R
import com.cla.wan.utils.ui.StatusUtils.getStatusBarHeight

/**
 * 标题栏
 */
class CustomTitleBar(context: Context, attr: AttributeSet? = null) :
    ConstraintLayout(context, attr) {

    val rlLeft: RelativeLayout by lazy { findViewById(R.id.rlLeft) }
    val rlCenter: RelativeLayout by lazy { findViewById(R.id.rlCenter) }
    val rlRight: RelativeLayout by lazy { findViewById(R.id.rlRight) }

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

