package com.cla.wan.utils.app

import android.util.Log
import com.cla.wan.utils.BuildConfig
import com.cla.wan.utils.config.UtilsConfig

object MyLog {

    private const val TAG = UtilsConfig.TAG
    private val DEBUG = BuildConfig.DEBUG

    fun i(tag: String, info: String, printStack: Boolean = false) {
        if (!DEBUG)
            return

        val iTag = "$TAG $tag"
        Log.i(iTag, info)

        if (printStack) {
            for (i in Thread.currentThread().stackTrace) {
                Log.i(iTag, " printStack $i")
            }
        }
    }


    fun d(tag: String, info: String, printStack: Boolean = false) {
        if (!DEBUG)
            return

        val iTag = "$TAG $tag"
        Log.d(iTag, info)

        if (printStack) {
            for (i in Thread.currentThread().stackTrace) {
                Log.d(iTag, " printStack $i")
            }
        }
    }

    fun e(tag: String, info: String, printStack: Boolean = false) {
        if (!DEBUG)
            return

        val iTag = "$TAG $tag"
        Log.e(iTag, info)

        if (printStack) {
            for (i in Thread.currentThread().stackTrace) {
                Log.e(iTag, " printStack $i")
            }
        }
    }
}