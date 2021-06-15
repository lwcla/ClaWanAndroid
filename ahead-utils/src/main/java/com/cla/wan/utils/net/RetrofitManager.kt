package com.cla.wan.utils.net

import androidx.lifecycle.liveData
import com.cla.wan.utils.app.AppUtils
import com.cla.wan.utils.app.MyLog
import com.cla.wan.utils.app.showToast
import com.cla.wan.utils.config.ServiceAddressHelper
import com.cla.wan.utils.config.StartUpConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.*

enum class RetrofitType {
    NORMAL,  //只从网络读取数据
    CACHE,   //从网络读取数据，如果连接失败，那么读取缓存数据
    FORCE_CACHE //只从缓存读取数据
}

enum class ResourceState {
    Loading, Failure, Success
}

data class Resource<T>(
    val state: ResourceState,
    val data: T?,
    val code: Int = 0,
    val message: String? = null
) {
    companion object {

        fun <T> loading() = Resource<T>(ResourceState.Loading, null, code = 0)

        fun <T> failure(message: String? = null) =
            Resource<T>(ResourceState.Failure, null, code = 0, message = message)

        fun <T> success(data: T, code: Int) =
            Resource(ResourceState.Success, data = data, code = code)
    }
}

data class CallResult<T>(
    val success: Boolean,
    val httpCode: Int,
    val result: T?,
    val message: String? = null
) {
    companion object {
        inline fun <T> join(vararg results: CallResult<*>, block: () -> T): CallResult<T> {

            if (results.find { !it.success } != null) {
                val message = results.find { !it.message.isNullOrBlank() }?.message ?: ""
                return CallResult(false, 0, null, message)
            }

            return CallResult(true, 200, block())
        }
    }
}

/**
 * 请求成功
 */
fun <T> CallResult<T>.suc() = success && result != null

fun <T> CallResult<T>.toResource() = if (suc()) {
    Resource.success(result, code = httpCode)
} else {
    Resource.failure(message = message)
}

suspend fun <T> Call<T>.callAwait(): CallResult<T> = withContext(Dispatchers.IO) {

    try {
        val response = execute()
        if (response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                val invocation = request().tag(Invocation::class.java)!!
                val method = invocation.method()
                val e =
                    KotlinNullPointerException("Response from " + method.declaringClass.name + '.' + method.name + " was null but response body type was declared as non-null")
                MyLog.e(javaClass.name, "callAwait $e")
                CallResult(false, 0, null, e.message)
            } else {
                CallResult(true, response.code(), body)
            }
        } else {
            val e = HttpException(response)
            MyLog.e(javaClass.name, "callAwait $e")
            CallResult(false, 0, null, e.message)
        }

    } catch (e: Exception) {
        e.printStackTrace()
        CallResult(false, 0, null, e.message)
    }
}

inline fun <reified S, reified T : Any> fetch(
    forceCache: Boolean = false,
    cache: Boolean = true,
    baseUrl: String = "",
    crossinline block: suspend S.() -> CallResult<T>
) = liveData(Dispatchers.IO) {

    //返回loading信息
    emit(Resource.loading<T>())

    val errorInfo = try {
        if (forceCache) {
            val forceCacheService =
                RetrofitManager.createService<S>(RetrofitType.FORCE_CACHE, baseUrl)
            val data = block(forceCacheService)
            if (data.suc()) {
                //这是从缓存中读取的数据，先返回让ui显示出来
                emit(data.toResource())
            }
        }

        val service = if (cache) {
            //这个会去读取网络数据，如果失败，则读取缓存数据
            RetrofitManager.createService<S>(RetrofitType.CACHE, baseUrl)
        } else {
            //这个只会从网络读取数据
            RetrofitManager.createService<S>(RetrofitType.NORMAL, baseUrl)
        }

        val data = block(service)
        if (data.suc()) {
            emit(data.toResource())
            return@liveData
        }

        data.message
    } catch (e: Exception) {
        e.printStackTrace()
        e.message
    }

    errorInfo?.showToast(tag = "Net Error")
    MyLog.d(javaClass.name, "fire errorInfo=$errorInfo")
    emit(Resource.failure<T>(message = errorInfo))
}

inline fun <reified S, reified T : Any> fire(
    forceCache: Boolean = false,
    cache: Boolean = true,
    baseUrl: String = "",
    crossinline block: suspend S.() -> Call<T>
) = fetch<S, T>(forceCache, cache, baseUrl) {
    block(this).callAwait()
}

/**
 * @author  zhouyufei
 * @date  2020/4/12 11:23 AM
 * @version 1.0
 */
object RetrofitManager {
    private val retrofitMap by lazy { RetrofitMap() }

    inline fun <reified T> createService(
        type: RetrofitType = RetrofitType.CACHE,
        baseUrl: String = ""
    ): T {
        val pair = getServiceMap(type, baseUrl)
        return getService(pair.first, pair.second)
    }

    fun getServiceMap(
        type: RetrofitType,
        baseUrl: String = ""
    ) = retrofitMap.getValue(baseUrl, type = type)

    inline fun <reified T> getService(
        retrofit: Retrofit,
        serviceCache: MutableMap<Class<*>, WeakReference<Any>>
    ): T {
        var serviceClass: Any? = null

        if (serviceCache.containsKey(T::class.java)) {
            serviceClass = serviceCache[T::class.java]?.get()
        }

        if (serviceClass == null) {
            serviceClass = retrofit.create(T::class.java)
            serviceCache[T::class.java] = WeakReference<Any>(serviceClass)
        }

        return serviceClass!! as T
    }
}

private class RetrofitMap {

    //这里写成这个样子只是因为楼管需要直接切换项目，不能杀死app来切换
    //这里就是根据baseUrl来获取不同的map，这个map装的是不同type对应的retrofit
    private val retrofitMap by lazy { mutableMapOf<String, RetrofitTypeMap>() }

    private val startUpConfig by lazy { StartUpConfig.impl }

    private val gson by lazy { GsonConverterFactory.create() }
    private val loggingInterceptor by lazy {
        //日志拦截器
        HttpLoggingInterceptor("Networking").apply {
            println("lwl RetrofitMap.debug?${AppUtils.isDebug()}")
            if (AppUtils.isDebug()) {
                setLogLevel(Level.INFO)
                setPrintLevel(HttpLoggingInterceptor.PrintLevel.BODY)
            } else {
                setLogLevel(Level.OFF)
                setPrintLevel(HttpLoggingInterceptor.PrintLevel.NONE)
            }
        }
    }

    fun getValue(
        baseUrl: String,
        type: RetrofitType
    ): Pair<Retrofit, MutableMap<Class<*>, WeakReference<Any>>> {
        var pair: Pair<Retrofit, RetrofitClassMap>? = null

        var url = baseUrl
        if (url.isBlank()) {
            url = ServiceAddressHelper.baseUrl()
        }

        //同一个url，retrofit会有网络和强制读取本地缓存的情况
        //这里需要区分不同的类别
        if (retrofitMap.containsKey(url)) {
            pair = retrofitMap[url]?.get(type)
        }

        if (pair != null) {
            return pair
        }

        synchronized(this) {
            if (pair == null) {
                pair = Pair(createRetrofit(type, url), RetrofitClassMap())
                println("lwl RetrofitMap.getValue synchronized create url=$url type=$type pair=$pair")
                val map = retrofitMap[url] ?: RetrofitTypeMap()
                map[type] = pair!!
                retrofitMap[url] = map
            }
        }

        return pair!!
    }

    private fun createRetrofit(type: RetrofitType, baseUrl: String): Retrofit = when (type) {
        RetrofitType.NORMAL -> initRetrofit(baseUrl = baseUrl) {
            addInterceptor(NetInterceptor())
        }

        RetrofitType.CACHE -> initRetrofit(baseUrl = baseUrl) {
            cache(getNetCache())
            addNetworkInterceptor(CacheInterceptor())
            addInterceptor(NetCacheInterceptor())
            connectTimeout(5, TimeUnit.SECONDS) //首页数据请求超时时间设置为5秒
        }

        RetrofitType.FORCE_CACHE -> initRetrofit(baseUrl = baseUrl) {
            cache(getNetCache())
            addInterceptor(ForceCacheInterceptor())
        }
    }

    private fun createSSLSocketFactory(): SSLSocketFactory {
        val sc: SSLContext = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf(TrustAllManager()), SecureRandom())
        return sc.socketFactory
    }

    private inline fun initRetrofit(
        baseUrl: String,
        block: OkHttpClient.Builder.() -> Unit
    ): Retrofit {

        //Okhttp对象
        val okHttpClient = with(OkHttpClient.Builder()) {

            this.block()

            addInterceptor(startUpConfig.getHeaderInterceptor())
            addInterceptor(loggingInterceptor)
            addInterceptor(TokenInterceptor())
            hostnameVerifier(TrustAllHostnameVerifier())
            sslSocketFactory(createSSLSocketFactory(), TrustAllManager())

            build()
        }

        //创建Retrofit对象
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gson)
            .build()
    }
}

private data class RetrofitClassMap(val classMap: MutableMap<Class<*>, WeakReference<Any>> = mutableMapOf()) :
    MutableMap<Class<*>, WeakReference<Any>> by classMap

private data class RetrofitTypeMap(val map: MutableMap<RetrofitType, Pair<Retrofit, RetrofitClassMap>> = mutableMapOf()) :
    MutableMap<RetrofitType, Pair<Retrofit, RetrofitClassMap>> by map

private class TrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String?, session: SSLSession?): Boolean {
        return true
    }
}

private class TrustAllManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}