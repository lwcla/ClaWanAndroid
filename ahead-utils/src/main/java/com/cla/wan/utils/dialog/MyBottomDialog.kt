package com.cla.wan.utils.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.cla.wan.utils.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 去掉默认背景的BottomSheetDialogFragment
 * 主要是有些时候需要做圆角，就继承这个dialog
 */
abstract class MyBottomDialog : BottomSheetDialogFragment() {

    var dismissListener: MyDialogDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        try {
            val layoutId = getLayoutId()
            if (layoutId != -1) {
                return inflater.inflate(getLayoutId(), container, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()

        //去掉默认背景
        try {
            dialog?.window?.findViewById<View>(R.id.design_bottom_sheet)
                ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        dismissListener?.invoke()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int
}