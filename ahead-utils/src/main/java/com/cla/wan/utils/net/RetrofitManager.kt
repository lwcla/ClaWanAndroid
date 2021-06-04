package com.cla.wan.utils.net

import com.cla.wan.utils.config.ModuleInfoHelper
import com.cla.wan.utils.config.StartUpConfig
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private typealias GetBaseUrl = () -> String

/**
 * @author  zhouyufei
 * @date  2020/4/12 11:23 AM
 * @version 1.0
 */
object RetrofitManager {

//    init {
//        ServerAddressModel().init()
//    }

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

    suspend fun <T> Call<T>.await() = suspendCoroutine<T?> { cont ->

        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val body = response.body()
                if (body == null) {
                    cont.resume(null)
                    return
                }

                cont.resume(body)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                cont.resumeWithException(t)
            }
        })
    }

    /**
     * 这里用call的同步方法，是为了抓到[TokenFailureException]这个异常
     * 用call的异步方法，会因为抓不到其他线程的异常，导致程序崩溃
     */
//    fun <T> exec(call: Call<NetResp<T>>): LiveData<NetResp<T>> {
//        var resp = MutableLiveData<NetResp<T>>()
//
//        GlobalScope.launch(Dispatchers.IO) {
//
//            try {
//                val response = call.execute()
//
//                launch(Dispatchers.Main.immediate) {
//                    if (response.code() == 204) {
//                        //处理delete请求时body为空的情况
//                        resp.value = NetResp("success", 200, Any(), 204) as NetResp<T>
//                    } else {
//                        val body = response.body()
//                        if (body == null) {
//                            resp.value =
//                                NetResp.fromJson(response.errorBody()?.string(), response.code())
//                        } else {
//                            body.httpCode = response.code()
//                            resp.value = body
//                        }
//                    }
//                }
//
//            } catch (e: TokenFailureException) {
//                e.printStackTrace()
//            } catch (e: Exception) {
//                e.printStackTrace()
//
//                launch(Dispatchers.Main.immediate) {
//                    resp.value = NetResp.fromJson(e.message, -1)
//                }
//            }
//        }
//
//        return resp
//    }

    /**
     * 加载缓存数据
     */
//    fun <T> cacheExec(call: Call<NetResp<T>>): LiveData<T> {
//
//        val resp = MutableLiveData<T>()
//
//        GlobalScope.launch(Dispatchers.IO) {
//            try {
//                val response = call.execute()
//                if (response.code() == 204) {
//                    //处理delete请求时body为空的情况
//                    resp.postValue(null)
//                    return@launch
//                }
//
//                val body = response.body()
//                if (body == null) {
//                    resp.postValue(null)
//                    return@launch
//                }
//
//                if (!body.success()) {
//                    resp.postValue(null)
//                    return@launch
//                }
//
//                resp.postValue(body.data)
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                resp.postValue(null)
//            }
//        }
//
//        return resp
//    }
}

//fun <T> Call<NetResp<T>>.toLiveData(): LiveData<NetResp<T>> {
//    return RetrofitManager.exec(this)
//}

private class RetrofitMap {

    private val startUpConfig by lazy { StartUpConfig.impl }
    private val gson by lazy { GsonConverterFactory.create() }

    //这里写成这个样子只是因为楼管需要直接切换项目，不能杀死app来切换
    //这里就是根据baseUrl来获取不同的map，这个map装的是不同type对应的retrofit
    private val retrofitMap by lazy {
        mutableMapOf<String, MutableMap<RetrofitType, Pair<Retrofit, MutableMap<Class<*>, WeakReference<Any>>>>>()
    }
    private val addressModel by lazy { ServerAddressModel() }

    private val loggingInterceptor by lazy {
        //日志拦截器
        with(HttpLoggingInterceptor("Networking")) {

            val debug = ModuleInfoHelper.readModuleInfo()?.debug ?: false

            if (debug) {
                setLogLevel(Level.INFO)
                setPrintLevel(HttpLoggingInterceptor.PrintLevel.BODY)
            } else {
                setLogLevel(Level.OFF)
                setPrintLevel(HttpLoggingInterceptor.PrintLevel.NONE)
            }

            this
        }
    }

    fun getValue(
        baseUrl: String,
        type: RetrofitType
    ): Pair<Retrofit, MutableMap<Class<*>, WeakReference<Any>>> {
        var pair: Pair<Retrofit, MutableMap<Class<*>, WeakReference<Any>>>? = null

        var url = baseUrl
        if (url.isBlank()) {
            url = addressModel.getActiveAddress().url
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
                val fit = createRetrofit(type, url)
                val cache = mutableMapOf<Class<*>, WeakReference<Any>>()
                pair = Pair(fit, cache)
                println("lwl RetrofitMap.getValue synchronized create pair=$pair")
                val map = retrofitMap[url] ?: mutableMapOf()
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

enum class RetrofitType {
    NORMAL, CACHE, FORCE_CACHE
}

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