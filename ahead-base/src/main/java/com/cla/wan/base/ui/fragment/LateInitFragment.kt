package com.cla.wan.base.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.cla.wan.base.R
import com.cla.wan.base.widget.MultipleStatusLayout

abstract class LateInitFragment : BaseFragment() {

    lateinit var rootView: MultipleStatusLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = MultipleStatusLayout(requireContext()).apply {
            contentViewLayoutId = getLayoutId()
            loadData = { loadData() }
            initView = { initView() }
            useDefaultLoadingView(requireContext())
            useDefaultErrorView(requireContext())

            emptyViewLayoutId = R.layout.layout_data_empty_new
            errorViewLayoutId = R.layout.layout_load_failure_new
            customStatusLayout(this)
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        if (!rootView.contentViewShowed) {
            rootView.reLoadData()
        } else {
            resume()
        }
    }

    /**
     * 子类加载完数据之后，调这个方法显示内容
     */
    fun showContent() {
        rootView.showContent()
    }

    fun isShowContent() = rootView.isShowContent()

    /**
     * 显示数据加载错误提示页面
     * @param force 是否强制显示错误页面，如果为true，那么不管当前是否已经显示content，都会切换为错误提示页面
     * 如果为false，那么当前页面为content时，不会切换
     */
    fun showError(force: Boolean = false) {
        rootView.showError(force)
    }

    fun showEmpty(force: Boolean = false) {
        rootView.showEmpty(force)
    }

    /**
     * 因为onResume有延迟初始化ui的操作
     * 如果需要在生命周期中做一些控件的操作，就重写这个方法，写在这里面
     */
    open fun resume() {

    }

    /**
     *  对MultipleStatusLayout添加一些自己的设置
     */
    open fun customStatusLayout(rootView: MultipleStatusLayout) {

    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * 在这个方法中加载数据
     */
    abstract fun loadData()

    /**
     * 在这个方法中做ui的初始化
     */
    abstract fun initView()
}