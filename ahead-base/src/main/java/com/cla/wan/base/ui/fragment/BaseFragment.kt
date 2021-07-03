package com.cla.wan.base.ui.fragment

import androidx.fragment.app.Fragment
import com.cla.wan.utils.app.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseFragment : Fragment() {

    private val jobMap = mutableMapOf<String, WeakReference<Job>>()

    val fragmentId by lazy { System.currentTimeMillis() }

    open fun refresh() {

    }

    fun launch(
        name: String,
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        jobMap.launch(this, name, context, start, block)
    }
}