package com.cla.scaffold.com.cla.scaffold.dialog

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import com.cla.scaffold.R
import com.cla.wan.base.config.SpKey
import com.cla.wan.base.dialog.BaseDialog
import com.cla.wan.base.utils.showToast
import com.cla.wan.utils.app.colorValue
import com.cla.wan.utils.data.saveData
import kotlinx.android.synthetic.main.dialog_protocol_agree_layout.*

/**
 * Author : LinkSy
 * Date : 2019-12-19.
 * Description : 用户协议提示窗
 *
 */
class ProtocolAgreeDialog : BaseDialog() {

    companion object {
        private val TAG = ProtocolAgreeDialog::class.java.simpleName
    }

    var agree: Boolean = false

    override fun getLayoutRes(): Int = R.layout.dialog_protocol_agree_layout

    override fun widthScale(): Float = 0.75f

    override fun heightScale() = 0.53f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        tvRefuse.setOnClickListener {
            agree = false
            dismissAllowingStateLoss()
        }

        tvAgree.setOnClickListener {
            SpKey.USER_AGREE_ALL_PROTOCOL.saveData(true)
            agree = true
            dismissAllowingStateLoss()
        }

        val spanOne = requireContext().getString(R.string.user_protocol_name)
        val spanTwo = requireContext().getString(R.string.privacy_policy_name)
        val string = requireContext().getString(R.string.protocol_tips_des)

        tvContent.movementMethod = LinkMovementMethod.getInstance()
        tvContent.text = SpannableString(string).apply {

            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        "查看用户协议".showToast(TAG)
                    }
                },
                string.indexOf(spanOne),
                string.indexOf(spanOne) + spanOne.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        "查看隐私协议".showToast(TAG)
                    }
                }, string.indexOf(spanTwo),
                string.indexOf(spanTwo) + spanTwo.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(
                ForegroundColorSpan(requireContext().colorValue(R.color.c1)),
                string.indexOf(spanOne),
                string.indexOf(spanOne) + spanOne.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            setSpan(
                ForegroundColorSpan(requireContext().colorValue(R.color.c1)),
                string.indexOf(spanTwo),
                string.indexOf(spanTwo) + spanTwo.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun onStop() {
        super.onStop()
        dismissListener?.invoke()
    }
}
