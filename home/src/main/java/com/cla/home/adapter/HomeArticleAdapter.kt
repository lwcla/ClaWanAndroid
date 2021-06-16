package com.cla.home.adapter

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cla.home.R
import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.classText
import com.cla.home.bean.owner
import com.cla.home.bean.timeByNow
import com.cla.wan.utils.adapter.MyBaseAdapter
import com.cla.wan.utils.app.*
import com.cla.wan.utils.ui.setSvgImage
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal typealias ShowArticleDetail = (HomeArticleData) -> Unit

internal typealias ClickArticle = (HomeArticleData) -> Unit

class HomeArticleAdapter(context: Context, private val owner: LifecycleOwner) :
    MyBaseAdapter<HomeArticleData>(context) {

    var showArticleDetail: ShowArticleDetail? = null
    var clickArticle: ClickArticle? = null

    override fun createHolder(
        parent: ViewGroup,
        viewType: Int
    ) = create(R.layout.adapter_home_article, parent,
        initHolder = {
            get<HomeTagContainerView>(R.id.llTag).owner = owner
            click<MaterialCardView>(R.id.cvContent) {
                bean?.let { data -> clickArticle?.invoke(data) }
            }
            longClick<MaterialCardView>(R.id.cvContent) {
                showArticleDetail?.let { show ->
                    bean?.let { data ->
                        show.invoke(data)
                        return@longClick true
                    }
                }
                false
            }
            clickBean<RelativeLayout>(R.id.rlCollect) {
                collect = !collect
                get<ImageView>(R.id.ivCollect).collect(this)
            }

        }) { holder, _ ->
        holder.get<TextView>(R.id.tvAuthor).text = owner()
        holder.get<TextView>(R.id.tvTitle).text = title.formHtml()
        holder.get<TextView>(R.id.tvDate).text = timeByNow()
        holder.get<TextView>(R.id.tvClass).text = classText()
        holder.get<ImageView>(R.id.ivCollect).collect(this)
        holder.get<HomeTagContainerView>(R.id.llTag).bind(this)
    }

    private fun ImageView.collect(bean: HomeArticleData) {
        setSvgImage(
            if (bean.collect) {
                R.drawable.svg_star
            } else {
                R.drawable.svg_un_star
            }, R.color.c1
        )
    }
}

internal class HomeTagContainerView(context: Context, attr: AttributeSet? = null) :
    LinearLayout(context, attr) {

    private val viewList = mutableListOf<HomeTagView>()
    var owner: LifecycleOwner? = null
    private var job: Job? = null

    init {
        this.orientation = HORIZONTAL
    }

    fun bind(bean: HomeArticleData) = bean.apply {
        job?.cancel()

        val dataList = mutableListOf<Pair<String, Boolean>>()
        if (isTop) {
            dataList.add(Pair("置顶", true))
        }
        if (fresh == true) {
            dataList.add(Pair("新", true))
        }
        tags.forEach { dataList.add(Pair(it.name, false)) }

        job = owner?.lifecycleScope?.launch(Dispatchers.Main.immediate) {
            val list = dataList.beyondViewList(viewList,
                createView = { HomeTagView(context) },
                initData = { view, data, pos ->
                    view.bind(data.first, data.second, pos == dataList.size - 1)
                })

            removeAllViews()
            visibility = if (list.isEmpty()) View.INVISIBLE else View.VISIBLE
            list.forEach { addView(it) }
        }
    }
}

internal class HomeTagView(context: Context, attr: AttributeSet? = null) :
    AppCompatTextView(context, attr) {

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.toFloat())
        setPadding(2.dp2px(), 2.dp2px(), 2.dp2px(), 2.dp2px())
    }

    fun bind(string: String, newStyle: Boolean = false, last: Boolean) {
        text = string
        if (newStyle) {
            setTextColor(context.colorValue(R.color.color_d70110))
            setBackgroundResource(R.drawable.border_d70110_tag_text)
        } else {
            setTextColor(context.colorValue(R.color.color_ff8000))
            setBackgroundResource(R.drawable.border_ff8000_tag_text)
        }

        val params = (layoutParams as? LinearLayout.LayoutParams?) ?: LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginEnd = if (last) 15.dp2px() else 5.dp2px()
        layoutParams = params
    }
}
