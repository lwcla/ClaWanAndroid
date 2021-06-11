package com.cla.wan.utils.app

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs


/**
 * 根据数据集合的数量 来确定当前显示的view个数
 * 如果数据比view的数量多，那么就需要创建view
 * 如果数据比view的数量少，那么就隐藏多余的view
 *
 * @return 返回当前可见的view的集合
 */
suspend inline fun <T, V : View> List<T>.beyondViewList(
    viewList: MutableList<V>,
    crossinline createView: () -> V,
    crossinline initData: (V, T, Int) -> Unit
): List<View> = withContext(Dispatchers.Default) {

    val visibleViewList = mutableListOf<View>()

    if (isEmpty()) {
        return@withContext visibleViewList
    }

    val beyondNum = size - viewList.size
    if (beyondNum > 0) {
        //view的数量还不够，需要新建
        repeat(beyondNum) {
            viewList.add(createView())
        }
    }

    launch(Dispatchers.Main.immediate) {
        if (beyondNum < 0) {
            //view的数量比数据还要多，隐藏多的view
            repeat(abs(beyondNum)) {
                //从集合中最后一个view往前开始隐藏
                val position = viewList.size - 1 - it
                if (position >= 0) {
                    viewList[position].visibility = View.GONE
                }
            }
        }

        repeat(size) {
            //设置数据
            val view = viewList[it]

            initData(view, get(it), it)
            view.visibility = View.VISIBLE

            visibleViewList.add(view)
        }
    }

    return@withContext visibleViewList
}