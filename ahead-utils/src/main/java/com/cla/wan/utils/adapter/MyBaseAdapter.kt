package com.cla.wan.utils.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.cla.wan.utils.app.dp2px
import java.util.*

typealias OnPreLoad = () -> Int

abstract class MyBaseAdapter<T>(val context: Context) :
    RecyclerView.Adapter<MyBaseViewHolder<T>>() {

    companion object {
        const val NORMAL_VIEW = 0x000
        const val END_VIEW = 0x001
    }

    val inflater by lazy { LayoutInflater.from(context) }
    val dataList = mutableListOf<T>()

    // 预加载回调
    var onPreload: OnPreLoad? = null

    // 预加载偏移量
    var preloadItemCount = -1

    //是否开启预加载，默认为true
    var preloadEnable = false

    //当前显示的页面下标
    private var currentPage = -1

    //需要加载的下一页的下标
    private var nextPage = 0

    // 列表滚动状态
    private var scrollState = SCROLL_STATE_IDLE

    /**
     * 数据是否已经加载完成
     */
    private var over = false

    val dataSize: Int
        get() = dataList.size

    open fun refreshData(list: List<T>) {
        setProCount(list)
        currentPage = -1
        over = false

        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    open fun addData(list: List<T>) {
        setProCount(list)

        val originalSize = dataList.size
        dataList.addAll(list)
        notifyItemRangeChanged(originalSize, dataSize - originalSize)
    }

    /**
     * 加载成功了
     */
    fun loadSuccess(page: Int = nextPage, over: Boolean = false) {
        this.over = over
        this.nextPage = page
    }

    /**
     * 如果加载失败了，那么就要重新加载下一页才行
     */
    fun loadFailed() {
        this.currentPage = -1
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // 更新滚动状态
                scrollState = newState
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBaseViewHolder<T> {

        if (END_VIEW == viewType) {
            return createOverHolder()
        }

        return createHolder(parent, viewType)
    }

    override fun getItemCount(): Int = if (!over) dataSize else dataSize + 1

    override fun getItemViewType(position: Int): Int = if (over && position == dataSize) {
        END_VIEW
    } else {
        NORMAL_VIEW
    }

    override fun onBindViewHolder(holder: MyBaseViewHolder<T>, position: Int) {
        checkPreload(position)
        holder.bind(covertData(dataList[position], position))
    }

    override fun onBindViewHolder(
        holder: MyBaseViewHolder<T>,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (over && position == dataSize) {
            if (holder is MyEndViewHolder) {
                holder.bind()
            }
            return
        }

        if (payloads.isNullOrEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.bind(covertData(dataList[position], position), payloads[0] as String)
        }
    }

    /**
     * 设置预加载偏移量，默认是每次加载数据的一半的量
     */
    private fun setProCount(list: List<T>) {
        if (preloadItemCount == -1) {
            preloadItemCount = (list.size / 3)
        }
    }

    // 判断是否进行预加载
    private fun checkPreload(position: Int) {
        if (!preloadEnable) {
            return
        }

        if (over) {
            return
        }

        if (scrollState == SCROLL_STATE_IDLE) {
            //列表正在滚动
            return
        }

        if (position != (itemCount - 1 - preloadItemCount).coerceAtLeast(0)) {
            // 索引值等于阈值
            return
        }

        //有可能还在加载下一页的数据，但是数据还没有返回，这个时候不要重复去加载数据
        if (nextPage == currentPage) {
            return
        }

        currentPage = nextPage
        nextPage = onPreload?.invoke() ?: 0
    }

    abstract fun createHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyBaseViewHolder<T>

    /**
     * 对数据做一个处理
     */
    open fun covertData(t: T, pos: Int): T = t

    protected fun Int.inflate(parent: ViewGroup): View =
        inflater.inflate(this, parent, false)

    inline fun create(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup,
        crossinline initHolder: MyBaseViewHolder<T>.() -> Unit = {},
        crossinline bindData: T.(MyBaseViewHolder<T>, String?) -> Unit
    ) = object : MyBaseViewHolder<T>(inflater.inflate(layoutRes, parent, false)) {

        init {
            initHolder()
        }

        override fun bind(t: T, payload: String?) {
            this.bean = t
            t.bindData(this, payload)
        }
    }

    private fun createOverHolder() = MyEndViewHolder<T>(TextView(context).apply {
        text = "已经到底了"
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setPadding(0, 10.dp2px(), 0, 10.dp2px())
        gravity = Gravity.CENTER
    })
}

private class MyEndViewHolder<T>(itemView: View) : MyBaseViewHolder<T>(itemView) {

    fun bind() {

    }

    override fun bind(t: T, payload: String?) {

    }
}

abstract class MyBaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val map = WeakHashMap<Int, View>()

    var bean: T? = null

    abstract fun bind(t: T, payload: String? = null)

    inline fun <reified T : View> get(
        @IdRes id: Int
    ): T = map[id]?.run { this as T } ?: synchronized(itemView) {
        val view = itemView.findViewById<T>(id)
        map[id] = view
        view
    }

    inline fun <reified V : View> clickBean(
        @IdRes id: Int,
        crossinline clickListener: T.() -> Unit
    ) {
        get<V>(id).setOnClickListener {
            bean?.apply { clickListener(this) }
        }
    }

    inline fun <reified T : View> click(
        @IdRes id: Int,
        clickListener: View.OnClickListener
    ) {
        get<T>(id).setOnClickListener(clickListener)
    }

    inline fun <reified T : View> longClick(
        @IdRes id: Int,
        clickListener: View.OnLongClickListener
    ) {
        get<T>(id).setOnLongClickListener(clickListener)
    }
}







