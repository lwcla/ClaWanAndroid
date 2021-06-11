package com.cla.wan.utils.ui

import android.view.View
import java.lang.ref.WeakReference

class ScaleViewHelper(
    target: View,
    private val pressedScale: Float = 0.8f,
    private val disabledScale: Float = 0.8f
) {

    private var mTarget: WeakReference<View> = WeakReference<View>(target)
    private val normalScale = 1.0f

    var changeScaleWhenPress = true
    var changeScaleWhenDisable = true
        set(value) {
            field = value
            mTarget.get()?.apply {
                onEnabledChanged(this, isEnabled)
            }
        }

    fun onPressedChanged(current: View, pressed: Boolean) {
        mTarget.get()?.apply {
            if (current.isEnabled) {
                val scale = if (changeScaleWhenPress && pressed && current.isClickable) {
                    pressedScale
                } else {
                    normalScale
                }

                scaleX = scale
                scaleY = scale

            } else if (changeScaleWhenDisable) {
                val scale = disabledScale
                scaleX = scale
                scaleY = scale
            }
        }
    }

    fun onEnabledChanged(current: View, enabled: Boolean) {
        mTarget.get()?.apply {
            val alphaForIsEnable = if (changeScaleWhenDisable) {
                if (enabled) normalScale else disabledScale
            } else {
                normalScale
            }
            if (current != this && isEnabled != enabled) {
                isEnabled = enabled
            }

            scaleX = alphaForIsEnable
            scaleY = alphaForIsEnable
        }
    }
}

interface ScaleViewInf {
    fun setChangeScaleWhenPress(var1: Boolean)

    fun setChangeScaleWhenDisable(var1: Boolean)
}