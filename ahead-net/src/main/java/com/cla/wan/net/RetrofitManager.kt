package com.cla.wan.net

import com.cla.wan.net.config.NetConfigHelper
import com.cla.wan.net.config.ServiceAddressHelper
import com.cla.wan.utils.app.AppUtils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import javax.net.ssl.*

enum class RetrofitType {
    ONLY_NET,  //只从网络读取数据
    CACHE,   //从网络读取数据，如果连接失败，那么读取缓存数据
    ONLY_CACHE //只从缓存读取数据
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
    ): T = create(T::class.java, type, baseUrl)

    fun <T> create(
        cls: Class<T>,
        type: RetrofitType = RetrofitType.CACHE,
        baseUrl: String = ""
    ): T {
        val pair = getServiceMap(type, baseUrl)

        val retrofit = pair.first
        val serviceCache = pair.second
        var serviceClass = serviceCache[cls]?.get()

        if (serviceClass == null) {
            serviceClass = retrofit.create(cls)
            serviceCache[cls] = WeakReference<Any>(serviceClass)
        }

        return serviceClass!! as T
    }

    private fun getServiceMap(
        type: RetrofitType,
        baseUrl: String = ""
    ) = retrofitMap.getValue(baseUrl, type = type)
}

private class RetrofitMap {

    private val gson by lazy { GsonConverterFactory.create() }

    //这里写成这个样子只是因为楼管需要直接切换项目，不能杀死app来切换
    //这里就是根据baseUrl来获取不同的map，这个map装的是不同type对应的retrofit
    private val retrofitMap by lazy {
        mutableMapOf<String, RetrofitTypeMap>()
    }

    private val loggingInterceptor by lazy {
        //日志拦截器
        HttpLoggingInterceptor("Networking").apply {
            val debug = AppUtils.isDebug()
            println("lwl RetrofitMap.debug?${debug}")
            if (debug) {
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
        RetrofitType.ONLY_NET -> initRetrofit(baseUrl = baseUrl) {
            addInterceptor(NetInterceptor())
        }

        RetrofitType.CACHE -> initRetrofit(baseUrl = baseUrl) {
            cache(getNetCache())
            addNetworkInterceptor(CacheInterceptor())
            addInterceptor(NetCacheInterceptor())
            connectTimeout(5, TimeUnit.SECONDS) //首页数据请求超时时间设置为5秒
        }

        RetrofitType.ONLY_CACHE -> initRetrofit(baseUrl = baseUrl) {
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

            NetConfigHelper.getHeaderInterceptor()?.let { addInterceptor(it) }
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