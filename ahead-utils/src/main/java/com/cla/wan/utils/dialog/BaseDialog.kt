package com.cla.wan.utils.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.cla.wan.utils.app.AppUtils.getScreenHeight
import com.cla.wan.utils.app.AppUtils.getScreenWidth

typealias MyDialogDismissListener = () -> Unit


abstract class BaseDialog : DialogFragment() {

    var dismissListener: MyDialogDismissListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(getLayoutRes(), container, false)
    }

    override fun onResume() {
        val width = (getScreenWidth() * widthScale()).toInt()
        val height = (getScreenHeight() * heightScale()).toInt()
        dialog?.window?.setLayout(width, height)
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        dismissListener?.invoke()
    }

    open fun widthScale(): Float = 1f

    open fun heightScale(): Float = 1f

    @LayoutRes
    abstract fun getLayoutRes(): Int
}