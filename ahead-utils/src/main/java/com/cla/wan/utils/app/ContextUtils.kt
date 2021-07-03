package com.cla.wan.utils.app

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 转换颜色值
 */
fun Context.colorValue(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)


fun MutableMap<String, WeakReference<Job>>.launch(
    owner: LifecycleOwner,
    name: String,
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
) {
    get(name)?.get()?.cancel()

    owner.lifecycleScope.launch {
        val job = launch(context, start = start, block = block)
        set(name, WeakReference(job))
    }
}

