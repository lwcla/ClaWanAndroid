package com.cla.wan.utils.app

import android.os.Build
import android.text.Html
import android.text.TextUtils
import java.util.*

fun String?.clearNull(): String {

    if (this.isNullOrBlank()) {
        return ""
    }

    if (TextUtils.equals(
            this.uppercase(Locale.getDefault()),
            "null".uppercase(Locale.getDefault())
        )
    ) {
        return ""
    }

    return this
}

fun String?.formHtml(): String {

    val text = clearNull()
    if (text.isBlank()) {
        return ""
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT).toString()
    } else {
        Html.fromHtml(text).toString()
    }
}