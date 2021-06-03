package com.cla.wan.base.ui.fragment

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    val fragmentId by lazy { System.currentTimeMillis() }

}