package com.cla.wan.utils.ui

import android.content.Context
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * 显示DialogFragment
 */
inline fun <reified T : DialogFragment> Context?.showDialogFragment(show: (FragmentManager, String) -> Unit) {

    if (this == null) {
        return
    }

    (this as? FragmentActivity?).showDialogFragment<T>(show)
}

/**
 * 显示DialogFragment
 */
inline fun <reified T : DialogFragment> FragmentActivity?.showDialogFragment(show: (FragmentManager, String) -> Unit) {

    if (this == null) {
        return
    }

    val tag = T::class.java.simpleName
    val dialog = supportFragmentManager.findFragmentByTag(tag) as? T?
    if (dialog != null) {
        val bt = supportFragmentManager.beginTransaction()
        bt.remove(dialog)
        bt.commitNowAllowingStateLoss()
    }

    if (!isFinishing && dialog?.isAdded != true) {
        show(supportFragmentManager, tag)
    }
}