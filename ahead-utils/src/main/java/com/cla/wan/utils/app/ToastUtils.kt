package com.cla.wan.base.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.cla.wan.utils.LifeCycleInjector
import com.cla.wan.utils.R
import com.cla.wan.utils.app.AppUtils.dp2px
import com.cla.wan.utils.app.colorValue

private val toastHandler by lazy { Handler(Looper.getMainLooper()) }
private val tagMap = mutableMapOf<String, Toast>()

/**
 * 使用同一个tag的toast不会重复弹出来
 */
fun String.showToast(tag: String? = null, duration: Int = Toast.LENGTH_SHORT) {
    LifeCycleInjector.appContext.toast(this, tag, duration)
}

fun Int.showToast(tag: String? = null, duration: Int = Toast.LENGTH_SHORT) {
    LifeCycleInjector.appContext.toast(this, tag, duration)
}

/**
 * 使用同一个tag的toast不会重复弹出来
 */
fun Context?.toast(
    @StringRes msgRes: Int?,
    tag: String? = null,
    duration: Int = Toast.LENGTH_SHORT
) {

    if (this == null || msgRes == null) {
        return
    }

    val msg = try {
        this.getString(msgRes)
    } catch (e: Exception) {
        null
    }

    if (msg.isNullOrEmpty()) {
        return
    }

    toast(msg, tag, duration)
}

/**
 * 使用同一个tag的toast不会重复弹出来
 * @param msg 需要提示的信息
 * @param tag 用来从toastMap获取toast的key，默认为当前的类名
 * @param duration toast显示的时长
 * @param time 是否需要增加时间限制，10s秒钟只显示一次
 */
fun Context?.toast(
    msg: String?,
    tag: String? = null,
    duration: Int = Toast.LENGTH_SHORT,
    time: Boolean = false
) {

    if (this == null || msg.isNullOrEmpty()) {
        return
    }

    toastHandler.post {
        var realTag = tag ?: this.javaClass.simpleName

        if (time) {
            //这个单独用一个tag来存储
            realTag += "time"
        }

        try {
            val toast = tagMap[realTag] ?: getCustomToast(this, msg, duration)
            toast.view?.findViewById<TextView>(R.id.tvContent)?.text = msg

            if (time) {
                //10s秒钟只显示一次
                val toastTime = toast.view?.getTag(R.id.last_toast_time) as? Long? ?: 0L
                val currentTime = System.currentTimeMillis()
                if (currentTime - toastTime <= 10000L) {
                    return@post
                }
                toast.view?.setTag(R.id.last_toast_time, currentTime)
            }

            toast.duration = duration
            tagMap[realTag] = toast
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 取消当前页面的toast
 */
fun Context?.cancelToast(tag: String? = null) {
    if (this == null) {
        return
    }

    toastHandler.post {
        val realTag = tag ?: this.javaClass.simpleName
        val toast = tagMap[realTag]
        toast?.cancel()
    }
}

private fun getCustomToast(
    context: Context,
    msg: String?,
    cDuration: Int = Toast.LENGTH_SHORT
) = Toast(context).apply {
    //android r 在后台情况下禁止自定义toast了
    view = TextView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        id = R.id.tvContent
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.toFloat())
        setTextColor(context.colorValue(R.color.black))

        val start = dp2px(15)
        val top = dp2px(7)
        setPadding(start, top, start, top)
        setBackgroundResource(R.drawable.common_toast_bg)

        text = msg
    }
    setGravity(Gravity.CENTER, 0, 0)
    duration = cDuration
}