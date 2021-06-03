package com.cla.wan.base.utils

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.cla.wan.base.R

/**
 * 把颜色设置为c1
 */
fun ContentLoadingProgressBar.setC1(ctx: Context) {
    //这样设置的c1才有效
    val c = ContextCompat.getColor(ctx, R.color.c1)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        indeterminateDrawable.colorFilter = BlendModeColorFilter(c, BlendMode.SRC_ATOP)
    } else {
        indeterminateDrawable.setColorFilter(c, PorterDuff.Mode.SRC_ATOP)
    }
}