package com.cla.wan.utils.data

import android.os.Parcelable
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ThreadUtils
import com.cla.wan.utils.LifeCycleInjector
import com.cla.wan.utils.config.MyLog
import com.tencent.mmkv.MMKV
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

fun String.saveData(
    value: Any?,
    listener: MMKVHelper.MMKVDataChangeListener? = null
): Boolean = value?.run {
    MMKVHelper.saveData(this@saveData, this, listener)
} ?: false


/**
 * 根据key加载数据
 */
fun String.loadData(
    defaultValue: Boolean = false
) = MMKVHelper.decodeBoolean(this, defaultValue)


/**
 * Created by fangsf on 2020/3/31
 * Useful:
 */
object MMKVHelper {

    interface MMKVDataChangeListener {
        fun dataChange(result: Boolean, any: Any)
    }

    private val mmkv by lazy {
        val path = MMKV.initialize(LifeCycleInjector.app)
        MyLog.i(javaClass.name, "mmkv 路径 $path")
        MMKV.defaultMMKV()
    }

    private val instance: MMKV?
        get() = if (LifeCycleInjector.app == null) {
            null
        } else {
            mmkv
        }

    val mutableMap by lazy { ConcurrentHashMap<String, CopyOnWriteArrayList<MMKVDataChangeListener>>() }

    /**
     * @param key 存储数据的 key
     * @param mmkvDataChangeListener 数据发生改变的回调监听
     */
    fun setDataChangeListener(key: String, mmkvDataChangeListener: MMKVDataChangeListener) {
        mutableMap[key] = mutableMap[key] ?: CopyOnWriteArrayList()
        val mutableList = mutableMap[key]
        mutableList?.let {
            if (!it.contains(mmkvDataChangeListener)) {
                it.add(mmkvDataChangeListener)
            }
        }
    }

    /**
     * 移除对应 Key 所有的监听
     */
    fun removeKeyDataChangeListener(key: String) {
        if (mutableMap.containsKey(key)) {
            mutableMap.remove(key)
        }
    }

    /**
     * 移除监听
     */
    fun removeDataChangeListener(
        key: String,
        mmkvDataChangeListener: MMKVDataChangeListener
    ) {
        if (mutableMap.containsKey(key)) {
            mutableMap.forEach {
                if (it.key == key) {
                    it.value.remove(mmkvDataChangeListener)
                }
            }
        }
    }


    /**
     * @param key 存储数据的key
     * @param value 存储的数据 value
     * @param mmkvDataChangeListener 设置数据发生改变监听，按需设置与否
     */
    fun saveData(
        key: String,
        value: Any,
        mmkvDataChangeListener: MMKVDataChangeListener? = null
    ): Boolean {
        mmkvDataChangeListener?.let { setDataChangeListener(key, it) }

        val result = when (value) {
            is String -> instance?.encode(key, value)
            is Float -> instance?.encode(key, value)
            is Boolean -> instance?.encode(key, value)
            is Int -> instance?.encode(key, value)
            is Long -> instance?.encode(key, value)
            is Double -> instance?.encode(key, value)
            is ByteArray -> instance?.encode(key, value)
            is Parcelable -> instance?.encode(key, value)
            is Nothing -> false
            else -> instance?.encode(key, GsonUtils.toJson(value))
        }

        mutableMap.forEach {
            if (key == it.key) {
                // 对应的数据发生改变需要通知
                val observerKeyList = it.value
                observerKeyList.forEach {
                    ThreadUtils.runOnUiThread { it.dataChange(result ?: false, value) }
                }
            }
        }

        MyLog.i(javaClass.name, "saveData key=$key value=$value-${value.javaClass} result?$result")
        return result ?: false
    }

    inline fun <reified T> encodeObj(key: String): T? = decodeString(key).run {
        if (isBlank()) {
            null
        } else {
            GsonUtils.fromJson(this, T::class.java)
        }
    }

    fun <T : Parcelable> encode(key: String, t: T?) {
        if (t == null) {
            return
        }
        instance?.encode(key, t)
    }

    fun encode(key: String, sets: Set<String>?) {
        if (sets == null) {
            return
        }
        instance?.encode(key, sets)
    }


    fun decodeInt(key: String, defaultValue: Int = 0): Int {
        val decodeInt = instance?.decodeInt(key, defaultValue)
        MyLog.i(javaClass.name, "decodeInt key=$key value=$decodeInt")
        return decodeInt ?: defaultValue
    }

    fun decodeDouble(key: String, defaultValue: Double = 0.00): Double {
        val decodeDouble = instance?.decodeDouble(key, defaultValue)
        MyLog.i(javaClass.name, "decodeDouble key=$key value=decodeDouble")
        return decodeDouble ?: defaultValue
    }

    fun decodeLong(key: String, defaultValue: Long = 0L): Long {
        val decodeLong = instance?.decodeLong(key, defaultValue)
        MyLog.i(javaClass.name, "decodeLong key=$key value=$decodeLong")
        return decodeLong ?: defaultValue
    }

    fun decodeBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val decodeBool = instance?.decodeBool(key, defaultValue)
        MyLog.i(javaClass.name, "decodeBoolean key=$key value=$decodeBool")
        return decodeBool ?: defaultValue
    }

    fun decodeFloat(key: String, defaultValue: Float = 0F): Float {
        val decodeFloat = instance?.decodeFloat(key, defaultValue)
        MyLog.i(javaClass.name, "decodeFloat key=$key value=$decodeFloat")
        return decodeFloat ?: defaultValue
    }

    fun decodeByteArray(key: String): ByteArray? {
        val decodeBytes = instance?.decodeBytes(key)
        MyLog.i(javaClass.name, "decodeByteArray key=$key value=$decodeBytes")
        return decodeBytes
    }

    fun decodeString(key: String, defaultValue: String = ""): String {
        val decodeString = instance?.decodeString(key, defaultValue)
        MyLog.i(javaClass.name, "decodeString key=$key value=$decodeString")
        return decodeString ?: defaultValue
    }

    fun <T : Parcelable> decodeParcelable(key: String, tClass: Class<T>): T? {
        val decodeParcelable = instance?.decodeParcelable(key, tClass)
        MyLog.i(javaClass.name, "decodeParcelable key=$key value=$decodeParcelable")
        return decodeParcelable
    }

    fun decodeStringSet(key: String): MutableSet<String>? {
        val decodeStringSet = instance?.decodeStringSet(key, Collections.emptySet())
        MyLog.i(javaClass.name, "decodeStringSet key=$key value=$decodeStringSet")
        return decodeStringSet
    }

    fun removeKey(key: String) {
        MyLog.i(javaClass.name, "removeKey ")
        instance?.removeValueForKey(key)
    }

    fun clearAll() {
        MyLog.i(javaClass.name, "clearAll ")
        instance?.clearAll()
    }
}