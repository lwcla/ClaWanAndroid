package com.cla.home.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.cla.home.R
import com.cla.home.bean.HomeArticleData
import com.cla.home.bean.owner
import com.cla.home.bean.timeByNow
import com.cla.wan.utils.adapter.MyBaseAdapter
import com.cla.wan.utils.adapter.MyBaseViewHolder
import com.cla.wan.utils.app.showToast
import com.google.android.material.card.MaterialCardView

class HomeArticleAdapter(context: Context) : MyBaseAdapter<HomeArticleData>(context) {

    override fun createHolder(parent: ViewGroup, viewType: Int): MyBaseViewHolder<HomeArticleData> {

        return create(R.layout.adapter_home_article, parent,
            initHolder = {
                click<MaterialCardView>(R.id.cvContent) { bean?.title?.showToast() }
            }) { holder ->
            holder.get<TextView>(R.id.tvAuthor).text = owner()
            holder.get<TextView>(R.id.tvName).text = title
            holder.get<TextView>(R.id.tvDate).text = timeByNow()
        }
    }
}
