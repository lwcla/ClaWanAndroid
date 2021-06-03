package com.cla.wan.utils.app

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat


/**
 * 转换颜色值
 */
fun Context.colorValue(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

