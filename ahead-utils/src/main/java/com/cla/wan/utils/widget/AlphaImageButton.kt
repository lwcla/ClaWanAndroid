package com.cla.wan.utils.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import cn.fhstc.utils.ui.AlphaViewHelper
import cn.fhstc.utils.ui.AlphaViewInf

/**
 * 按压效果的imageButton
 */
class AlphaImageButton(context: Context, attr: AttributeSet? = null) :
    AppCompatImageButton(context, attr), AlphaViewInf {

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