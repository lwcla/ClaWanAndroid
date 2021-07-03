package com.cla.scaffold.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.scaffold.R
import com.cla.scaffold.com.cla.scaffold.ui.fragment.Test1Fragment
import com.cla.scaffold.com.cla.scaffold.vm.MainVm
import com.cla.wan.base.config.BaseConfig.MAIN_PAGE_EXIT_APP_KEY
import com.cla.wan.base.config.BaseConfig.MAIN_PAGE_GO_HONE
import com.cla.wan.base.config.BaseConfig.MAIN_PAGE_GO_MINE
import com.cla.wan.base.config.BaseConfig.MAIN_PAGE_LOGOUT_KEY
import com.cla.wan.base.config.BaseConfig.MAIN_PAGE_SHOW_CONTENT
import com.cla.wan.base.config.HomePath
import com.cla.wan.base.config.ScaffoldPath
import com.cla.wan.base.config.SpKey
import com.cla.wan.base.ui.BaseAty
import com.cla.wan.base.ui.fragment.BaseFragment
import com.cla.wan.base.utils.Utils
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.app.createVm
import com.cla.wan.utils.data.loadBool
import com.cla.wan.utils.ui.statusColor
import com.cla.wan.utils.ui.toDrawable
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

@Route(path = ScaffoldPath.MAIN_ACTIVITY)
class MainAty : BaseAty() {

    companion object {

        /**
         * 启动主页
         *
         * @param context context
         * @param finish 是否关闭主页
         * @param logout 退出登录，清空登录信息，打开登录页面
         * @param showContent 直接显示主页内容
         */
        fun lunch(
            context: Context,
            finish: Boolean = false,
            logout: Boolean = false,
            showContent: Boolean = false
        ) {
            Utils.toMainAty(context) {
                withBoolean(MAIN_PAGE_EXIT_APP_KEY, finish)
                withBoolean(MAIN_PAGE_LOGOUT_KEY, logout)
                withBoolean(MAIN_PAGE_SHOW_CONTENT, showContent)
            }
        }
    }

    private val mainVm by lazy { createVm<MainVm>() }

    private val initLayout by lazy {
        //把背景图去掉
        window.decorView.setBackgroundResource(R.color.transparent)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        statusColor(R.color.transparent, light = true, fitWindow = false)

        setTheme(R.style.MainActivityStyleNormal)
        setContentView(R.layout.activity_main)
    }

    private val inflater by lazy { LayoutInflater.from(this) }
    private val tabData by lazy {
        val homeFragment = lazy { ARouterUtil.navigation(HomePath.HOME_FRAGMENT) as? BaseFragment? }
        val myFragment = lazy { Test1Fragment() }
        listOf(
            Pair(TabData("首页", R.drawable.sbp_svg_main_home), homeFragment),
            Pair(TabData("我的", R.drawable.sbp_svg_main_mine), myFragment)
        )
    }
    private val initViewPager by lazy { initViewPager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        statusColor(R.color.transparent, light = true, fitWindow = false)
        super.onCreate(savedInstanceState)
    }

    override fun setup() {
        //用户是否同意协议
        val isAgree = SpKey.USER_AGREE_ALL_PROTOCOL.loadBool()
        if (!isAgree) {
            //GuideActivity是透明的，所以这里需要设置背景图
            window.decorView.setBackgroundResource(R.drawable.splash)
            GuideAty.lunch(this)
            return
        }

        initView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent?.getBooleanExtra(MAIN_PAGE_EXIT_APP_KEY, false) == true) {
            //退出app
            super.onBackPressed()
            finishAffinity()
            exitProcess(0)
            return
        }

        if (intent?.getBooleanExtra(MAIN_PAGE_LOGOUT_KEY, false) == true) {
            //退出登录
//            logout()
            return
        }

        if (intent?.getBooleanExtra(MAIN_PAGE_SHOW_CONTENT, false) == true) {
            //显示主页
//            showMainContent()
            return
        }

        if (intent?.getBooleanExtra(MAIN_PAGE_GO_HONE, false) == true) {
            //要防止这个时候mainFragment还没有被添加到视图/或者当前视图中的fragment和mainFragment并不是同一个对象
            //如果是从登陆页过来的话，mainFragment这时还没有被添加到视图中的
//            showMainContent()
//            (mainFragment as? MainFragment?)?.goHome()
            return
        }

        if (intent?.getBooleanExtra(MAIN_PAGE_GO_MINE, false) == true) {
            //要防止这个时候mainFragment还没有被添加到视图/或者当前视图中的fragment和mainFragment并不是同一个对象
//            showMainContent()
//            (mainFragment as? MainFragment?)?.goMine()
            return
        }

//        if (intent?.getBooleanExtra(MAIN_PAGE_SHARE_KEY, false) == true) {
//            //来自浏览器跳转的分享内容
//            handleShareJump(intent)
//            return
//        }

        initView()
        initView()
    }

    private fun initView() {
        initLayout
        initViewPager
    }

    @SuppressLint("InflateParams")
    private fun initViewPager() {

        viewPager.isUserInputEnabled = false
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = tabData.size
            override fun createFragment(position: Int): Fragment {
                return tabData[position].second.value ?: Test1Fragment()
            }

            override fun getItemId(position: Int): Long {
                val fragmentId = tabData[position].second.value?.fragmentId
                return fragmentId ?: super.getItemId(position)
            }

            override fun containsItem(itemId: Long): Boolean {
                return tabData.map { it.second.value?.fragmentId }.contains(itemId)
            }
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val view = inflater.inflate(R.layout.sbp_fragment_main_layout_item, null)

            val iv = view.findViewById<ImageView>(R.id.iv)
            val tv = view.findViewById<TextView>(R.id.tv)
            val holder = TabHolder(iv, tv)

            val data = tabData[position].first
            tab.tag = data
            tab.customView = view
            tab.customView?.tag = holder

            //默认选中第一个tab
            val colorRes = if (position == 0) R.color.c1 else R.color.color_d8d8d8
            tv.text = data.name
            tv.setTextColor(ContextCompat.getColor(this, colorRes))
            iv.setImageDrawable(data.iconRes.toDrawable(this, colorRes))
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    //刷新首页
                    val fragment = tabData[0].second
                    if (fragment.isInitialized()) {
                        fragment.value?.refresh()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val data = tab?.tag as? TabData?
                val holder = tab?.customView?.tag as? TabHolder?

                val icon = data?.iconRes?.toDrawable(this@MainAty, R.color.color_d8d8d8)
                holder?.iv?.setImageDrawable(icon)
                val color = ContextCompat.getColor(this@MainAty, R.color.color_d8d8d8)
                holder?.tv?.setTextColor(color)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val data = tab?.tag as? TabData?
                val holder = tab?.customView?.tag as? TabHolder?

                val icon = data?.iconRes?.toDrawable(this@MainAty, R.color.c1)
                holder?.iv?.setImageDrawable(icon)

                val color = ContextCompat.getColor(this@MainAty, R.color.c1)
                holder?.tv?.setTextColor(color)
            }
        })
    }
}

private data class TabData(val name: String, @DrawableRes val iconRes: Int)
private class TabHolder(val iv: ImageView?, val tv: TextView?)