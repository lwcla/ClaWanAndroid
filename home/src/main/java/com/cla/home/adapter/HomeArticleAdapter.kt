package com.cla.home.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.blankj.utilcode.util.TimeUtils
import com.cla.home.R
import com.cla.home.bean.HomeArticleData
import com.cla.wan.utils.adapter.MyBaseAdapter
import com.cla.wan.utils.adapter.MyBaseViewHolder
import com.cla.wan.utils.app.showToast
import com.google.android.material.card.MaterialCardView
import java.util.*

class HomeArticleAdapter(context: Context) : MyBaseAdapter<HomeArticleData>(context) {

    override fun createHolder(parent: ViewGroup, viewType: Int): MyBaseViewHolder<HomeArticleData> {

        return create(R.layout.adapter_home_article, parent,
            initHolder = {
                get<MaterialCardView>(R.id.cvContent).setOnClickListener { bean?.title?.showToast() }
            }, bindData = { bean ->
                bean.apply {
                    get<TextView>(R.id.tvAuthor).text = if (!author.isNullOrBlank()) {
                        "作者:$author"
                    } else if (!shareUser.isNullOrBlank()) {
                        "分享人:$shareUser"
                    } else {
                        ""
                    }

                    get<TextView>(R.id.tvName).text = bean.title

                    get<TextView>(R.id.tvDate).text = TimeUtils.getFriendlyTimeSpanByNow(
                        Date(bean.shareDate ?: System.currentTimeMillis())
                    )
                }

            })
    }
}
