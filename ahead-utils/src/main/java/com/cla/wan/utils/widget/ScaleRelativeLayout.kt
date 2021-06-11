package com.cla.wan.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.cla.wan.utils.ui.ScaleViewHelper
import com.cla.wan.utils.ui.ScaleViewInf

/**
 * 按压效果的RelativeLayout
 */
class ScaleRelativeLayout(context: Context, attr: AttributeSet? = null) :
    RelativeLayout(context, attr), ScaleViewInf {

    private val scaleViewHelper by lazy { ScaleViewHelper(this) }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        scaleViewHelper.onPressedChanged(this, pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        scaleViewHelper.onEnabledChanged(this, enabled)
    }

    override fun setChangeScaleWhenPress(var1: Boolean) {
        scaleViewHelper.changeScaleWhenPress = var1
    }

    override fun setChangeScaleWhenDisable(var1: Boolean) {
        scaleViewHelper.changeScaleWhenDisable = var1
    }
}