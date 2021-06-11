package com.cla.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cla.wan.utils.ui.load
import com.cla.home.R
import com.cla.home.bean.HomeBannerBean
import com.youth.banner.adapter.BannerAdapter

/**
 * 首页banner
 */
internal class HomeBannerAdapter(
    context: Context,
    list: List<HomeBannerBean>
) : BannerAdapter<HomeBannerBean, HomeBannerViewHolder>(list) {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): HomeBannerViewHolder {
        val view = inflater.inflate(R.layout.layout_home_banner, parent, false)
        return HomeBannerViewHolder(view)
    }

    override fun onBindView(
        holder: HomeBannerViewHolder?,
        data: HomeBannerBean?,
        position: Int,
        size: Int
    ) {
        data?.let { holder?.bind(it, size, position) }
    }
}

internal class HomeBannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val imageView by lazy { itemView.findViewById<ImageView>(R.id.iamgeView) }
    private val tvTitle by lazy { itemView.findViewById<TextView>(R.id.tvTitle) }
    private val tvIndicator by lazy { itemView.findViewById<TextView>(R.id.tvIndicator) }

    @SuppressLint("SetTextI18n")
    fun bind(bean: HomeBannerBean, dataSize: Int, position: Int) {
        imageView.load(bean.imagePath)
        tvTitle.text = bean.title
        tvIndicator.text = "${position + 1}/$dataSize"
    }
}

