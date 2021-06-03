package com.cla.wan.utils

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.cla.wan.utils.proxy.IAppLifeCycle
import com.cla.wan.utils.proxy.ILifecycleInjector


class LifeCycleInjector : ILifecycleInjector, IAppLifeCycle {

    companion object {
        var app: Application? = null
        val appContext: Context?
            get() = app?.applicationContext
    }

    override fun injectAppLifeCycle(
        application: Application?,
        appLifeCycles: MutableList<IAppLifeCycle>
    ) {
        appLifeCycles.add(this)
    }

    override fun injectActivityLifeCycle(
        application: Application?,
        activityLifecycleCallbacks: MutableList<Application.ActivityLifecycleCallbacks>
    ) {
    }

    override fun injectFragmentLifeCycle(
        application: Application?,
        fragmentLifecycleCallbacks: MutableList<FragmentManager.FragmentLifecycleCallbacks>
    ) {
    }

    override fun priority() = 0

    override fun onAttach(base: Context) {

    }

    override fun onCreate(application: Application) {
        app = application
    }

    override fun onTerminate(application: Application) {

    }
}