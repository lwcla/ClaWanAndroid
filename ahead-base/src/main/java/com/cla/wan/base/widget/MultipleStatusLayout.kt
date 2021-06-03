package com.cla.wan.base.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.cla.wan.base.R
import com.cla.wan.base.utils.setC1
import com.cla.wan.utils.app.AppUtils.dp2px
import com.cla.wan.utils.app.colorValue

/**
 * 加载数据
 */
typealias LoadData = () -> Unit

/**
 * 初始化界面元素
 */
typealias InitView = () -> Unit

typealias InitLoadView = (View) -> Unit
typealias InitEmptyView = (View) -> Unit
typealias InitErrorView = (View) -> Unit
typealias InitNoNetworkView = (View) -> Unit

class MultipleStatusLayout(context: Context, attr: AttributeSet? = null) :
    FrameLayout(context, attr) {

    companion object {
        const val SHOW_CONTENT = 0x001
        const val SHOW_LOADING = 0x002
        const val SHOW_EMPTY = 0x003
        const val SHOW_ERROR = 0x004
        const val SHOW_NO_NETWORK = 0x005

        const val INVALID_ID = -1
    }

    private val inflater = LayoutInflater.from(context)

    @LayoutRes
    var contentViewLayoutId: Int? = null

    @LayoutRes
    var loadViewLayoutId: Int? = null

    @LayoutRes
    var emptyViewLayoutId: Int? = null

    @LayoutRes
    var errorViewLayoutId: Int? = null

    @LayoutRes
    var noNetworkViewLayoutId: Int? = null

    var contentView: View? = null
    var loadingView: View? = null
    var emptyView: View? = null
    var errorView: View? = null
    var noNetworkView: View? = null

    var contentViewParams: LayoutParams? = null
    var loadingViewParams: LayoutParams? = null
    var emptyViewParams: LayoutParams? = null
    var errorViewParams: LayoutParams? = null
    var noNetworkViewParams: LayoutParams? = null

    /**
     * 加载数据的方法，如果设置了这个方法
     * 那么在显示loadingView的时候就会同步去调用这个方法
     * 在点击emptyView,errorView,noNetworkView的时候，默认会显示loadView，也会调用这个方法
     */
    var loadData: LoadData? = null

    var initView: InitView? = null

    var initLoadView: InitLoadView? = null
    var initEmptyView: InitEmptyView? = null
    var initErrorView: InitErrorView? = null
    var initNoNetworkView: InitNoNetworkView? = null

    private
    val defaultViewClickListener = OnClickListener {
        showLoading(delayToLoadData = 500, needLoadData = true)
    }

    var emptyViewClickListener: OnClickListener? = defaultViewClickListener
    var errorViewClickListener: OnClickListener? = defaultViewClickListener
    var onNetworkViewClickListener: OnClickListener? = defaultViewClickListener

    private val contentShowView = lazy {
        val view = getView(contentView, contentViewLayoutId, contentViewParams, null)
        if (view != null) {
            initView?.invoke()
        }
        view
    }
    private val loadingShowView = lazy {
        val view = getView(loadingView, loadViewLayoutId, loadingViewParams, null)
        if (view != null) {
            initLoadView?.invoke(view)
        }
        view
    }
    private val emptyShowView = lazy {
        val view = getView(emptyView, emptyViewLayoutId, emptyViewParams, emptyViewClickListener)
        if (view != null) {
            initEmptyView?.invoke(view)
        }
        view
    }
    private val errorShowView = lazy {
        val view = getView(errorView, errorViewLayoutId, errorViewParams, errorViewClickListener)
        if (view != null) {
            initErrorView?.invoke(view)
        }
        view
    }
    private val noNetworkShowView = lazy {
        val view = getView(
            noNetworkView,
            noNetworkViewLayoutId,
            noNetworkViewParams,
            onNetworkViewClickListener
        )
        if (view != null) {
            initNoNetworkView?.invoke(view)
        }
        view
    }

    private val viewList = listOf(
        Pair(SHOW_LOADING, loadingShowView),
        Pair(SHOW_EMPTY, emptyShowView),
        Pair(SHOW_ERROR, errorShowView),
        Pair(SHOW_NO_NETWORK, noNetworkShowView),
        Pair(SHOW_CONTENT, contentShowView)
    )

    private var currentStatus: Int = -1
    private var animatorSet: AnimatorSet? = null

    /**
     * 是否已经显示过contentView
     */
    val contentViewShowed: Boolean
        get() = contentShowView.isInitialized()

    val isShowLoading: Boolean
        get() = currentStatus == SHOW_LOADING

    init {

        isFocusable = false
        isClickable = false

        val typeArray = context.obtainStyledAttributes(attr, R.styleable.MultipleStatusLayout)
        contentViewLayoutId =
            typeArray.getResourceId(R.styleable.MultipleStatusLayout_contentLayoutId, INVALID_ID)
        loadViewLayoutId =
            typeArray.getResourceId(R.styleable.MultipleStatusLayout_loadingLayoutId, INVALID_ID)
        emptyViewLayoutId =
            typeArray.getResourceId(R.styleable.MultipleStatusLayout_emptyLayoutId, INVALID_ID)
        errorViewLayoutId =
            typeArray.getResourceId(R.styleable.MultipleStatusLayout_errorLayoutId, INVALID_ID)
        noNetworkViewLayoutId =
            typeArray.getResourceId(R.styleable.MultipleStatusLayout_noNetworkLayoutId, INVALID_ID)

        val useDefaultLoading =
            typeArray.getBoolean(R.styleable.MultipleStatusLayout_useDefaultLoading, false)
        if (useDefaultLoading) {
            useDefaultLoadingView(context)
        }

        typeArray.recycle()
//        setBackgroundResource(R.color.c11)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorSet?.cancel()
    }

    /**
     * 使用默认的加载view
     *  @param context context
     *  @param force 是否强制执行，如果为false的话，那么当前有设置了loadingView的情况下，是不会设置为默认的加载view的
     */
    fun useDefaultLoadingView(context: Context, force: Boolean = false) {

        if (!force) {
            if ((loadViewLayoutId != null && loadViewLayoutId != INVALID_ID) || loadingView != null) {
                return
            }
        }

        loadingView = RelativeLayout(context).apply {
            val ctx = ContextThemeWrapper(context, R.style.MyContentProgressBar)
            addView(ContentLoadingProgressBar(ctx).apply {
                val size = dp2px(44)
                val params = RelativeLayout.LayoutParams(size, size)
                params.addRule(RelativeLayout.CENTER_IN_PARENT)
                layoutParams = params
                setC1(context)

                isClickable = false
                isFocusable = false
            })
        }
    }

    /**
     * 使用默认定义的errorView
     * @param context context
     * @param force 是否强制执行，如果为false的话，那么当前有设置了errorView的情况下，是不会设置为默认的errorView
     */
    fun useDefaultErrorView(context: Context, force: Boolean = false) {

        if (!force) {
            if ((errorViewLayoutId != null && errorViewLayoutId != INVALID_ID) || errorView != null) {
                return
            }
        }

        errorView = LinearLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER

            addView(TextView(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

                val errorDrawable =
                    ContextCompat.getDrawable(context, R.mipmap.placeholder_empty_net)
                compoundDrawablePadding = dp2px(20)
                setCompoundDrawablesRelativeWithIntrinsicBounds(null, errorDrawable, null, null)

                text = "数据加载失败，点我重试"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColor(context.colorValue(R.color.black))
            })
        }
    }

    /**
     * 使用默认定义的noNetworkView
     * @param context context
     * @param force 是否强制执行，如果为false的话，那么当前有设置了noNetworkView的情况下，是不会设置为默认的noNetworkView
     */
    fun useDefaultNoNetworkView(context: Context, force: Boolean = false) {

        if (!force) {
            if ((noNetworkViewLayoutId != null && noNetworkViewLayoutId != INVALID_ID) || noNetworkView != null) {
                return
            }
        }

        noNetworkView = with(TextView(context)) {
            text = "网络未连接，请检查网络"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            gravity = Gravity.CENTER
            setTextColor(context.colorValue(R.color.black))
            this
        }
    }

    /**
     * 使用默认定义的emptyView
     * @param context context
     * @param force 是否强制执行，如果为false的话，那么当前有设置了emptyView的情况下，是不会设置为默认的emptyView
     */
    fun useDefaultEmptyView(context: Context, force: Boolean = false) {

        if (!force) {
            if ((emptyViewLayoutId != null && emptyViewLayoutId != INVALID_ID) || emptyView != null) {
                return
            }
        }

        emptyView = with(TextView(context)) {
            text = "这里没有数据"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTextColor(context.colorValue(R.color.black))
            gravity = Gravity.CENTER
            this
        }
    }

    fun setContentView(view: View, params: LayoutParams) {
        contentView = view
        contentViewParams = params
    }

    fun setLoadingView(view: View, params: LayoutParams) {
        loadingView = view
        loadingViewParams = params
    }

    fun setEmptyView(view: View, params: LayoutParams) {
        emptyView = view
        emptyViewParams = params
    }

    fun setErrorView(view: View, params: LayoutParams) {
        errorView = view
        errorViewParams = params
    }

    fun setNoNetworkView(view: View, params: LayoutParams) {
        noNetworkView = view
        noNetworkViewParams = params
    }

    /**
     * 当前是否为contentView
     */
    fun isShowContent() = currentStatus == SHOW_CONTENT

    fun showContent() {
        switchStatus(SHOW_CONTENT)
    }

    /**
     * 显示loadView
     * @param force 是否强制显示loadingView，如果为true，那么不管当前是否已经显示content，都会切换为loadingView
     * @param delayToLoadData 延迟多长时间去调[loadData],有的时候需要 延迟一点时间去加载数据，避免再次加载失败得太快，看起来就像是点击没有效果一样
     * @param needLoadData 是否需要在显示loadingView时，调用loadDate方法加载数据
     * @param forceLoad 强制加载，即使当前已经是loadingView也去执行加载
     */
    fun showLoading(
        force: Boolean = false,
        delayToLoadData: Long = 0,
        needLoadData: Boolean = false,
        forceLoad: Boolean = false
    ) {

        if (!force && !forceLoad && isShowContent()) {
            return
        }

        //避免重复调用时，会重复loadData
        if (currentStatus == SHOW_LOADING && !forceLoad) {
            return
        }

        switchStatus(SHOW_LOADING)

        if (needLoadData) {
            loadData?.let {
                postDelayed({ it.invoke() }, delayToLoadData)
            }
        }
    }

    /**
     * 刷新数据
     * 增加这个方法是为了防止页面重复去调用自己的loadData方法
     * 很多时候需要在activity的onResume方法中调用loadData方法，但是在onCreate中已经调了showLoading方法
     * 那么就可以在onResume中用这个方法
     *
     * 而在onCreate中用showLoading这个方法就是想用delayToLoadData参数，避免刚打开页面的时候，加载的过程一闪而过
     *
     * @param forceLoad 强制加载
     * @param forceShowLoading 是否需要显示loadingView
     */
    fun reLoadData(
        forceLoad: Boolean = false,
        forceShowLoading: Boolean = !isShowContent(),
        delayToLoadData: Long = 0
    ) {

        //避免重复调用时，会重复loadData
        if (!forceLoad && currentStatus == SHOW_LOADING) {
            return
        }

        if (forceShowLoading) {
            switchStatus(SHOW_LOADING)
        }

        loadData?.let {
            postDelayed({ it.invoke() }, delayToLoadData)
        }
    }

    /**
     * 显示没有数据提示页面
     * @param force 是否强制显示没有数据提示页面，如果为true，那么不管当前是否已经显示content，都会切换为没有数据提示页面
     * 如果为false，那么当前页面为content时，不会切换
     */
    fun showEmpty(force: Boolean = false) {
        if (!force && isShowContent()) {
            return
        }

        switchStatus(SHOW_EMPTY)
    }

    /**
     * 显示数据加载错误提示页面
     * @param force 是否强制显示错误页面，如果为true，那么不管当前是否已经显示content，都会切换为错误提示页面
     * 如果为false，那么当前页面为content时，不会切换
     */
    fun showError(force: Boolean = false) {
        if (!force && isShowContent()) {
            return
        }

        switchStatus(SHOW_ERROR)
    }

    fun showNoNetwork() {
        switchStatus(SHOW_NO_NETWORK)
    }

    /**
     * 切换当前的状态
     */
    private fun switchStatus(status: Int) {

        if (currentStatus == status) {
            return
        }

        val showingView = viewList.findLast { it.first == currentStatus }
        currentStatus = status

        //加一个动画，避免loadingView 突然切换到contentView，有闪屏的感觉
        animatorSet?.cancel()

        if (/*contentViewShowed ||*/ showingView?.second?.value == null || status != SHOW_CONTENT) {
            showView(status)
        } else {
            val contentView = viewList.find { it.first == status }?.second?.value
            if (contentView == null) {
                showView(status)
                return
            }

            val hideAnimator = showingView.second.value!!.run {
                ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            //把这个视图设置为不能获取焦点，避免在渐变的动画过程中，挡住contentView的焦点事件
                            //比如美食店铺页的滚动事件
                            this@run.isFocusable = false
                            this@run.isClickable = false
                        }
                    })

                    startDelay = 100
                }
            }

            val showAnimator = contentView.run {
                this.alpha = 0f
                ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            this@run.visibility = View.VISIBLE
                            this@run.isFocusable = true
                            this@run.isClickable = true
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            showView(status)
                        }
                    })
                }
            }

            animatorSet = AnimatorSet().apply {
                duration = 500
                play(showAnimator).with(hideAnimator)
                start()
            }
        }
    }

    private fun showView(status: Int) {
        viewList.forEach { pair ->
            pair.apply {
                if (first == status) {
                    val showView = second.value ?: return@forEach
                    if (showView.visibility != View.VISIBLE) {
                        showView.visibility = View.VISIBLE
                    }
                    showView.alpha = 1f
                    showView.isFocusable = true
                    showView.isClickable = true
                } else {
                    //如果视图没有初始化的话，就不要去动它
                    if (second.isInitialized()) {
                        //否则的话，就隐藏它
                        second.value?.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun getParams(params: LayoutParams?) =
        params ?: LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

    /**
     * 初始化view
     */
    @SuppressLint("ResourceType")
    private fun getView(
        view: View?,
        @LayoutRes layoutId: Int?,
        params: LayoutParams?,
        viewClickListener: OnClickListener?
    ): View? {

        if (view != null) {
            addView(view, 0, getParams(params))
            view.setOnClickListener(viewClickListener)
            return view
        }

        if (layoutId == null || layoutId == INVALID_ID) {
            return null
        }

        return try {
            val inflaterView = inflater.inflate(layoutId, this, false)
            addView(inflaterView, 0, getParams(params))
            inflaterView.setOnClickListener(viewClickListener)

            inflaterView
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}