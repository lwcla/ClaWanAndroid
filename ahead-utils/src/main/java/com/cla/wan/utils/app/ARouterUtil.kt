package com.cla.wan.utils.app

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.cla.wan.utils.BuildConfig
import com.cla.wan.utils.LifeCycleInjector

/**
 * ================================================
 * Created by JessYan on 30/03/2018 17:16
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
object ARouterUtil {

    private val instance: ARouter by lazy {
        // 配置必须在init之前否则将无效
        // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        if (BuildConfig.IS_DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }

        ARouter.init(LifeCycleInjector.app)
        ARouter.getInstance()
    }

    fun getARouter() = if (LifeCycleInjector.app == null) {
        null
    } else {
        instance
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 这个方法因为没有使用 [Activity]跳转
     * 所以 [ARouter] 会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [ARouter.getInstance] 并传入 [Activity]
     *
     * @param path `path`
     */
    fun navigation(path: String?): Any? {
        val aRouter = getARouter() ?: return null
        return aRouter.build(path).navigation()
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 这个方法因为没有使用 [Activity]跳转
     * 所以 [ARouter] 会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [ARouter.getInstance] 并传入 [Activity]
     *
     * @param path `path`
     */
    inline fun navigation(
        activity: Activity?,
        path: String,
        requestCode: Int,
        putExtra: Postcard.() -> Unit = {}
    ) {

        val aty = activity ?: return
        val aRouter = getARouter() ?: return

        val postcard = aRouter.build(path)
        putExtra.invoke(postcard)
        postcard.navigation(aty, requestCode)
    }

    /**
     * 使用 [ARouter] 根据 `path` 跳转到对应的页面, 如果参数 `context` 传入的不是 [Activity]
     * [ARouter] 就会自动给 [android.content.Intent] 加上 Intent.FLAG_ACTIVITY_NEW_TASK
     * 如果不想自动加上这个 Flag 请使用 [Activity] 作为 `context` 传入
     *
     * @param context
     * @param path
     */
    inline fun navigation(
        context: Context?,
        path: String?,
        navigationCallback: NavigationCallback? = null,
        putExtra: Postcard.() -> Unit = {}
    ): Any? {

        val ctx = context ?: return null
        val aRouter = getARouter() ?: return null

        val postcard = aRouter.build(path)
        putExtra.invoke(postcard)
        return postcard.navigation(ctx, navigationCallback)
    }
}