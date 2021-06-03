package com.cla.home

import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.wan.base.config.HomePath
import com.cla.wan.base.ui.fragment.LateInitFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Route(path = HomePath.HOME_FRAGMENT)
class HomeFragment : LateInitFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun loadData() {
        lifecycleScope.launch {
            delay(3000)
            showContent()
        }
    }

    override fun initView() {

    }
}