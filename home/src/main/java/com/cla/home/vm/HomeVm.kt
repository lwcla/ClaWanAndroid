package com.cla.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.cla.wan.utils.net.Resource
import com.cla.wan.utils.net.ResourceState

class HomeVm : ViewModel() {

    private val repo = HomeRepository()

    var nextPage = 1
    private val _loadArticle = MutableLiveData<Int>()
    private val _refreshPage = MutableLiveData<Any>()

    val loadArticle = _loadArticle.switchMap { nextPage ->
        repo.loadHomeData(nextPage).map {

            val listContainer = it.data?.data
            val curPage = listContainer?.curPage ?: 0

            if (it.state == ResourceState.Success) {
                this.nextPage = curPage
            }

            Resource(state = it.state, data = listContainer, code = it.code, message = it.message)
        }
    }

    val refreshPage = _refreshPage.switchMap {
        repo.refreshData().apply { nextPage = 1 }
    }

    fun refreshHomeData() {
        _refreshPage.value = ""
    }

    /**
     *  首页文章列表
     */
    fun loadArticle() {
        _loadArticle.value = nextPage
    }
}