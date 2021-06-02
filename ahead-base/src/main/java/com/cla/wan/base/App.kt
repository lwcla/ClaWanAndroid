package com.cla.wan.base

import android.app.Application
import android.content.Context
import cn.fhstc.utils.proxy.AppProxy
import cn.fhstc.utils.proxy.IAppLifeCycle
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        private lateinit var context: App
        fun instance() = context

        private var foreground = false

        /**
         * app是否在前台
         */
//        suspend fun isForeground(): Boolean = with(Dispatchers.Default) {
//
//            if (foreground) {
//                return@with true
//            }
//
//            //前一个activity onPause方法与被启动的activity的onResume方法之间会有一点时间差
//            //测试出来在100到600ms之间不定
//            //这里延迟一秒钟再去检查这个状态，以保证应用在前台的时候，变量的状态是对的
//            delay(1000)
//
//            return@with foreground
//        }
    }

    private lateinit var appProxy: IAppLifeCycle

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        appProxy = AppProxy(this)
        appProxy.onAttach(base)
    }

    @DelicateCoroutinesApi
    override fun onCreate() {
        context = this
        super.onCreate()
        appProxy.onCreate(this)

        GlobalScope.launch {
            //https://github.com/JeremyLiao/SmartEventBus/blob/master/docs/bus_all.md
            //LiveEventBus是基于LiveData实现，接收消息只能在主线程完成，发送消息可以在主线程或者后台线程发送。
            LiveEventBus.config().apply {
                lifecycleObserverAlwaysActive(true)  //配置LifecycleObserver（如Activity）接收消息的模式（默认值true
                autoClear(false) //配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存
            }

        }
    }

    override fun onTerminate() {
        super.onTerminate()
        appProxy.onTerminate(this)
    }
}