package com.cla.scaffold.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.cla.wan.utils.ui.load
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.scaffold.R
import com.cla.scaffold.adapter.ImgPagerAdapter
import com.cla.scaffold.com.cla.scaffold.dialog.ProtocolAgreeDialog
import com.cla.wan.base.config.ScaffoldPath
import com.cla.wan.base.ui.BaseAty
import com.cla.wan.utils.app.ARouterUtil
import com.cla.wan.utils.dialog.showDialogFragment
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * 引导页
 */
@Route(path = ScaffoldPath.GUIDE_ACTIVITY)
class GuideAty : BaseAty() {

    companion object {
        private val TAG = GuideAty::class.java.simpleName

        private val imgList = intArrayOf(
            R.mipmap.guide_1, R.mipmap.guide_2,
            R.mipmap.guide_3, R.mipmap.guide_4
        )

        fun lunch(context: Context) {
            ARouterUtil.navigation(context, ScaffoldPath.GUIDE_ACTIVITY) {
                withTransition(R.anim.guide_ani_enter, R.anim.guide_ani_exit)
            }
        }
    }

//    private val guideHelper by lazy { ARouterUtil.navigation(ConnectorPath.GUIDE_HELPER_IMPL) as? GuideHelper? }

    private var mAdapter: ImgPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.guide_ani_enter, 0)
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.guide_ani_exit)
        MainAty.lunch(this, true)
    }

    override fun setup() {
        lifecycleScope.launchWhenCreated {
            //暂停看Logo
            takeSomeTimeOff()
            showDialog()
        }
    }

    /**
     * 暂停一段时间
     */
    private suspend fun takeSomeTimeOff() = withContext(Dispatchers.Default) {
        delay(500)
    }

    /**
     * 显示用户协议提示窗
     */
    private fun showDialog() {
        showDialogFragment<ProtocolAgreeDialog> { manager, tag ->
            ProtocolAgreeDialog().apply {
                dismissListener = {
                    if (!agree) {
                        MainAty.lunch(this@GuideAty, finish = true)
                    } else {
                        initView()
                    }
                }

                show(manager, tag)
            }
        }
    }

    private fun initView() {
        showGuide()
    }

    /**
     * 显示引导程序
     */
    private fun showGuide() {
        setContentView(R.layout.activity_guide)

        val list = mutableListOf<ImageView>()
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        for (resId in imgList) {
            val iv = ImageView(this)
            iv.layoutParams = layoutParams
            iv.scaleType = ImageView.ScaleType.CENTER_CROP
            iv.load(resId)
            list.add(iv)
        }
        mAdapter = ImgPagerAdapter(list)
        vp.adapter = mAdapter
        initListener()
    }

    private fun initListener() {
        vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {

            }

            override fun onPageSelected(i: Int) {
                changeDot(i)
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        tvGo.setOnClickListener {
            MainAty.lunch(this)
        }
    }

    private fun changeDot(position: Int) {
        tvGo.visibility = if (position == imgList.lastIndex) View.VISIBLE else View.GONE
    }
}