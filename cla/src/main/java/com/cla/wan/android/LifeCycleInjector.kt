package com.cla.wan.android

import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentManager
import com.cla.wan.utils.proxy.IAppLifeCycle
import com.cla.wan.utils.proxy.ILifecycleInjector


class LifeCycleInjector : ILifecycleInjector, IAppLifeCycle {

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

    override fun priority() = 1

    override fun onAttach(base: Context) {

    }

    override fun onCreate(application: Application) {

    }

    override fun onTerminate(application: Application) {

    }
}