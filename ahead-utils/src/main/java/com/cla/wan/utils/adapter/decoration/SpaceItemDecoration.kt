package com.cla.wan.utils.adapter.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.cla.wan.utils.app.dp2px

/**
 * Author : LinkSy
 * Date : 2019-06-18.
 * Description :
 */
class SpaceRoundItemDecoration(space: Int) : SpaceItemDecoration(space, space, space, space)

/**
 *
 */
open class SpaceItemDecoration(top: Int = 0, start: Int = 0, end: Int = 0, bottom: Int = 0) :
    ItemDecoration() {

    private val t = top.dp2px()
    private val s = start.dp2px()
    private val e = end.dp2px()
    private val b = bottom.dp2px()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = t
        outRect.left = s
        outRect.right = e
        outRect.bottom = b
    }
}