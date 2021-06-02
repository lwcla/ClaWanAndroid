package com.cla.wan.utls.proxy

import android.content.Context
import android.content.pm.PackageManager
import cn.fhstc.utils.proxy.ILifecycleInjector
import com.cla.wan.utils.R

class ManifestParser(private val context: Context) {
    private val moduleValue: String = context.getString(R.string.lifecycle_proxy)

    fun parse(): List<ILifecycleInjector> {
        val modules = mutableListOf<ILifecycleInjector>()
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA
            )

            appInfo.metaData?.let {
                for (key in it.keySet()) {
                    if (moduleValue == it[key]) {
                        modules.add(parseModule(key))
                    }
                }
            }
            // 根据 priority 对扫描结果顺序进行调整，确定先初始化什么后初始化什么
            modules.sortWith { o1: ILifecycleInjector, o2: ILifecycleInjector -> o1.priority() - o2.priority() }
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Unable to find metadata to parse ConfigModule", e)
        }
        return modules
    }

    companion object {
        private fun parseModule(className: String): ILifecycleInjector {
            val clazz: Class<*> = try {
                Class.forName(className)
            } catch (e: ClassNotFoundException) {
                throw IllegalArgumentException("Unable to find ConfigModule implementation", e)
            }
            val module: Any = try {
                clazz.newInstance()
            } catch (e: InstantiationException) {
                val info = "Unable to instantiate ConfigModule implementation for $clazz"
                throw RuntimeException(info, e)
            } catch (e: IllegalAccessException) {
                val info = "Unable to instantiate ConfigModule implementation for $clazz"
                throw RuntimeException(info, e)
            }
            if (module !is ILifecycleInjector) {
                throw RuntimeException("Expected instanceof ConfigModule, but found: $module")
            }
            return module
        }
    }
}