package com.cla.wan.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.cla.wan.utils.ui.AlphaViewHelper
import com.cla.wan.utils.ui.AlphaViewInf

/**
 * 按压效果的RelativeLayout
 */
class AlphaRelativeLayout(context: Context, attr: AttributeSet? = null) :
    RelativeLayout(context, attr), AlphaViewInf {

    private val alphaViewHelper by lazy { AlphaViewHelper(this) }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        alphaViewHelper.onPressedChanged(this, pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        alphaViewHelper.onEnabledChanged(this, enabled)
    }

    override fun setChangeAlphaWhenPress(var1: Boolean) {
        alphaViewHelper.changeAlphaWhenPress = var1
    }

    override fun setChangeAlphaWhenDisable(var1: Boolean) {
        alphaViewHelper.changeAlphaWhenDisable = var1
    }
}