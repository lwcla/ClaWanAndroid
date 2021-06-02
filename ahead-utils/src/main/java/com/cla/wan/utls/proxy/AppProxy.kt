package cn.fhstc.utils.proxy

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import com.cla.wan.utls.proxy.ManifestParser
import java.lang.ref.WeakReference


class AppProxy(app: Application) : IAppLifeCycle {

    private val applicationRef = WeakReference(app)

    private var mAppLifeCycles = mutableListOf<IAppLifeCycle>()
//    private var mActivityLifeCycles = mutableListOf<Application.ActivityLifecycleCallbacks>()
//    private var mFragmentLifecycleCallbacks =
//        mutableListOf<FragmentManager.FragmentLifecycleCallbacks>()

    //用handler是为了保证整个流程是顺序执行的
    private val myHandler by lazy {
        val handlerThread = HandlerThread("AppProxy").apply {
            start()
        }

        MyHandler(this, handlerThread.looper)
    }

    init {
        myHandler.sendEmptyMessage(MyHandler.INIT)
    }

    private fun injectManifest() {
        val application = applicationRef.get() ?: return

        //这一块是通过反射去拿到的对象，比较耗时，让它在线程里面执行
        val injectors = ManifestParser(application).parse()
        for (injector in injectors) {
            injector.injectAppLifeCycle(application, mAppLifeCycles)
//            injector.injectActivityLifeCycle(application, mActivityLifeCycles)
//            injector.injectFragmentLifeCycle(application, mFragmentLifecycleCallbacks)
        }
    }

    private fun attach() {
        val application = applicationRef.get() ?: return

        for (appLifeCycle in mAppLifeCycles) {
            appLifeCycle.onAttach(application)
        }
    }

    private fun create() {
        val application = applicationRef.get() ?: return

        for (appLifeCycle in mAppLifeCycles) {
            appLifeCycle.onCreate(application)
        }

//        for (callbacks in mActivityLifeCycles) {
//            application.registerActivityLifecycleCallbacks(callbacks)
//        }
    }

    private fun terminate() {
        val application = applicationRef.get() ?: return

        for (appLifeCycle in mAppLifeCycles) {
            appLifeCycle.onTerminate(application)
        }

//        for (callbacks in mActivityLifeCycles) {
//            application.unregisterActivityLifecycleCallbacks(callbacks)
//        }
    }

    override fun onAttach(base: Context) {
        myHandler.sendEmptyMessage(MyHandler.ATTACH)
    }

    override fun onCreate(application: Application) {
        myHandler.sendEmptyMessage(MyHandler.CREATE)
    }

    override fun onTerminate(application: Application) {
        myHandler.sendEmptyMessage(MyHandler.TERMINATE)
    }

    private class MyHandler(appProxy: AppProxy, looper: Looper) : Handler(looper) {

        companion object {
            const val INIT = 0x001
            const val ATTACH = 0x002
            const val CREATE = 0x003
            const val TERMINATE = 0x004
        }

        private val reference = WeakReference(appProxy)

        override fun handleMessage(msg: Message) {

            val appProxy = reference.get() ?: return

            when (msg.what) {
                INIT -> appProxy.injectManifest()
                ATTACH -> appProxy.attach()
                CREATE -> appProxy.create()
                TERMINATE -> appProxy.terminate()
            }
        }
    }
}