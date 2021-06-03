package com.cla.wan.base.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

abstract class BaseAty : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
    }

    /**
     * 放在onCreate中的初始化方法
     */
    abstract fun setup()
}