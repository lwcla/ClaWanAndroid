package com.cla.wan.utils.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class MyBaseAdapter<T>(context: Context) :
    RecyclerView.Adapter<MyBaseViewHolder<T>>() {

    val inflater by lazy { LayoutInflater.from(context) }
    val dataList = mutableListOf<T>()

    val dataSize: Int
        get() = dataList.size

    open fun refreshData(list: List<T>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    open fun addData(list: List<T>) {
        val originalSize = dataList.size
        dataList.addAll(list)
        notifyItemRangeChanged(originalSize, dataSize - originalSize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBaseViewHolder<T> {
        return createHolder(parent, viewType)
    }

    override fun getItemCount(): Int = dataSize

    override fun onBindViewHolder(holder: MyBaseViewHolder<T>, position: Int) {
        holder.bind(covertData(dataList[position], position))
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
        crossinline bindData: MyBaseViewHolder<T>.(T) -> Unit
    ) = object : MyBaseViewHolder<T>(inflater.inflate(layoutRes, parent, false)) {

        init {
            initHolder()
        }

        override fun bind(t: T) {
            this.bean = t
            bindData(t)
        }
    }
}

abstract class MyBaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val map = WeakHashMap<Int, View>()

    var bean: T? = null

    abstract fun bind(t: T)

    inline fun <reified T : View> get(
        @IdRes id: Int
    ): T = map[id]?.run { this as T } ?: synchronized(itemView) {
        val view = itemView.findViewById<T>(id)
        map[id] = view
        view
    }
}





