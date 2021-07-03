package com.cla.home.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap

class HomeVm : ViewModel() {

    private val repo = HomeRepository()

    var nextPage = 1
    private val _loadArticle = MutableLiveData<Int>()
    private val _refreshPage = MutableLiveData<Any>()

    val loadArticle = _loadArticle.switchMap { nextPage ->
        repo.loadHomeData(nextPage) {
            val listContainer = it
            val curPage = listContainer.curPage
            println("lwl HomeVm.nextPage=${this@HomeVm.nextPage} curPage=$curPage")
            this@HomeVm.nextPage = curPage
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