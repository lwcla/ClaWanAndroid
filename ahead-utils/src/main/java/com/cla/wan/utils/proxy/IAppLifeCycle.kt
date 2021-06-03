package com.cla.wan.utils.proxy

import android.app.Application
import android.content.Context


interface IAppLifeCycle {
    fun onAttach(base: Context)

    fun onCreate(application: Application)

    fun onTerminate(application: Application)
}