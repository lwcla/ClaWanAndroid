package com.cla.home

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.home.vm.HomeVm
import com.cla.wan.base.config.HomePath
import com.cla.wan.base.ui.fragment.LateInitFragment
import com.cla.wan.utils.app.AppUtils.dp2px
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Route(path = HomePath.HOME_FRAGMENT)
class HomeFragment : LateInitFragment() {

    private val homeVm by lazy { ViewModelProvider(this).get(HomeVm::class.java) }

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun loadData() {
        lifecycleScope.launch {
            delay(2000)
            showContent()
        }
    }

    override fun initView() {
        val list = mutableListOf<String>()
        repeat(50) {
            list.add(it.toString())
        }

        rvData.layoutManager = LinearLayoutManager(requireContext())
        rvData.adapter = MyAdapter(list)
    }
}

private class MyAdapter(private val dataList: List<String>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val context = parent.context
        val textView = TextView(context).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setPadding(0, dp2px(15), 0, dp2px(15))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            gravity = Gravity.CENTER
        }

        return MyViewHolder(textView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    private class MyViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {

        fun bind(name: String) {
            textView.text = name
        }
    }
}