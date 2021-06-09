package com.cla.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.cla.home.bean.HomePageBean
import com.cla.wan.utils.net.Resource

class HomeVm : ViewModel() {

    private val repo = HomeRepository()

    private var page = 0
    private val _loadArticle = MutableLiveData<Int>()
    private val _refreshPage = MutableLiveData<Any>()

    val loadArticle = _loadArticle.switchMap { nextPage ->
        repo.loadHomeData(nextPage).map {
            val listContainer = it.data?.data

            page = listContainer?.curPage ?: 0
            Resource(state = it.state, data = listContainer, code = it.code, message = it.message)
        }
    }

    val refreshPage = _refreshPage.switchMap {


    }

    fun refreshHomeData() {
        page = 0
        _refreshPage.value = ""
    }

    /**
     *  首页文章列表
     */
    fun loadArticle() {
        _loadArticle.value = page++
    }

    /**
     * 加载banner数据
     */
    fun loadBanner() = repo.loadBanner()
}