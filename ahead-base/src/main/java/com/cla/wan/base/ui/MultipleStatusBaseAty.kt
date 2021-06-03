package com.cla.wan.base.ui

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.children
import androidx.core.widget.ContentLoadingProgressBar
import cn.fhstc.utils.ui.statusColor
import com.cla.wan.base.R
import com.cla.wan.base.utils.setC1
import com.cla.wan.base.widget.MultipleStatusLayout
import com.cla.wan.base.widget.SimpleTitleBar

abstract class MultipleStatusBaseAty : BaseAty() {

    protected val rootView: MultipleStatusLayout by lazy {
        MultipleStatusLayout(this).apply {
            contentViewLayoutId = getLayoutRes()
            emptyViewLayoutId = R.layout.layout_data_empty_new
            loadViewLayoutId = R.layout.layout_loading_new
            errorViewLayoutId = R.layout.layout_load_failure_new
            loadData = { loadData() }
            initView = { initView() }
            initLoadView = { initOtherTitleBar(it, "") }
            initEmptyView = { initOtherTitleBar(it, "提示") }
            initErrorView = { initOtherTitleBar(it, "提示") }
            initNoNetworkView = { initOtherTitleBar(it, "提示") }
            setBackgroundResource(bgColorRes)
            customStatusLayout(this)
            showLoading(
                force = true,
                needLoadData = needLoadData(),
                delayToLoadData = delayToLoadData()
            )
        }
    }

    //为了统一背景色和标题栏的颜色
    protected val bgColorRes = R.color.c9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView)
        statusColor(bgColorRes, light = true, fitWindow = false)
    }

    override fun setup() {

    }

    /**
     * 初始化其中布局中的标题
     */
    protected fun initOtherTitleBar(view: View, title: String) {
        val layout = view as? ViewGroup? ?: return
        layout.children.forEach { if (it is ContentLoadingProgressBar) it.setC1(this) }
        val titleBar = layout.children.find { it is SimpleTitleBar } as? SimpleTitleBar? ?: return
        titleBar.setTitle(title)
    }

    @LayoutRes
    abstract fun getLayoutRes(): Int
    abstract fun loadData()
    abstract fun initView()

    /**
     * 延迟去加载数据的时间
     * 如果不想延迟就设置为0
     */
    open fun delayToLoadData() = 0L

    /**
     *  对MultipleStatusLayout添加一些自己的设置
     */
    open fun customStatusLayout(rootView: MultipleStatusLayout) {

    }

    open fun needLoadData() = true
}