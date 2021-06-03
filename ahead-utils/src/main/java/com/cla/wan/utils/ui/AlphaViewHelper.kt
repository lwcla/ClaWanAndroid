package cn.fhstc.utils.ui

import android.view.View
import java.lang.ref.WeakReference

class AlphaViewHelper(
    target: View,
    private val pressedAlpha: Float = 0.5f,
    private val disabledAlpha: Float = 0.5f
) {

    private var mTarget: WeakReference<View> = WeakReference<View>(target)
    private val normalAlpha = 1.0f

    var changeAlphaWhenPress = true
    var changeAlphaWhenDisable = true
        set(value) {
            field = value
            mTarget.get()?.apply {
                onEnabledChanged(this, isEnabled)
            }
        }

    fun onPressedChanged(current: View, pressed: Boolean) {
        mTarget.get()?.apply {
            if (current.isEnabled) {
                alpha = if (changeAlphaWhenPress && pressed && current.isClickable) {
                    pressedAlpha
                } else {
                    normalAlpha
                }
            } else if (changeAlphaWhenDisable) {
                alpha = disabledAlpha
            }
        }
    }

    fun onEnabledChanged(current: View, enabled: Boolean) {
        mTarget.get()?.apply {
            val alphaForIsEnable = if (changeAlphaWhenDisable) {
                if (enabled) normalAlpha else disabledAlpha
            } else {
                normalAlpha
            }
            if (current != this && isEnabled != enabled) {
                isEnabled = enabled
            }
            alpha = alphaForIsEnable
        }
    }
}

interface AlphaViewInf {
    fun setChangeAlphaWhenPress(var1: Boolean)

    fun setChangeAlphaWhenDisable(var1: Boolean)
}