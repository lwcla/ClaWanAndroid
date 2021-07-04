package com.cla.wan.net

import androidx.lifecycle.liveData
import com.blankj.utilcode.util.GsonUtils
import com.cla.wan.utils.app.MyLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Invocation


/**
 * 网络的基础扩展方法
 */
object RequestEt {
    /**
     * liveData的方式请求数据
     */
    inline fun <reified Service, reified ReturnType> request(
        noinline call: suspend (Service.() -> CallResult<ReturnType>),
        noinline builder: RequestBuilder<Service, ReturnType, ReturnType>.() -> Unit = {}
    ) = request(
        createService = { type, baseUrl -> RetrofitManager.createService(type, baseUrl) },
        call = call,
        mapResult = { params -> toResource(params.processBean) },
        builder = builder
    )

    /**
     * liveData的方式请求数据
     *
     * @param Service Retrofit的Service
     * @param ResponseType 网络请求时返回的数据类型
     * @param ReturnType 实际上返回的数据类型
     *
     * @param mapResult  通过这个方法可以把 [ResponseType]类型的 的数据转换为 [ReturnType]类型 的数据，并且返回的是[Resource]数据，eg:CallResult<BaseBean<String>> -> Resource<String> ,去掉了BaseBean这一层
     * @param call 请求数据的方法
     */
    fun <Service, ResponseType, ReturnType> request(
        cls: Class<Service>,
        call: suspend (Service.() -> CallResult<ResponseType>),
        mapResult: CallResult<ResponseType>.(RequestBuilder<Service, ResponseType, ReturnType>) -> Resource<ReturnType>,
        builder: RequestBuilder<Service, ResponseType, ReturnType>.() -> Unit = {}
    ) = request(
        createService = { type, baseUrl -> RetrofitManager.create(cls, type, baseUrl) },
        call = call,
        mapResult = mapResult,
        builder = builder
    )

    /**
     * liveData的方式请求数据
     *
     * @param Service Retrofit的Service
     * @param ResponseType 网络请求时返回的数据类型
     * @param ReturnType 实际上返回的数据类型
     *
     * @param mapResult  通过这个方法可以把 [ResponseType]类型的 的数据转换为 [ReturnType]类型 的数据，并且返回的是[Resource]数据，eg:CallResult<BaseBean<String>> -> Resource<String> ,去掉了BaseBean这一层
     * @param call 请求数据的方法
     */
    fun <Service, ResponseType, ReturnType> request(
        createService: (RetrofitType, String) -> Service,
        call: suspend (Service.() -> CallResult<ResponseType>),
        mapResult: CallResult<ResponseType>.(RequestBuilder<Service, ResponseType, ReturnType>) -> Resource<ReturnType>,
        builder: RequestBuilder<Service, ResponseType, ReturnType>.() -> Unit = {}
    ) = liveData(Dispatchers.IO) {

        val params = RequestBuilder<Service, ResponseType, ReturnType>().apply { builder(this) }
        val baseUrl = params.baseUrl
        //如果是只读网络的话，那么就不要去读缓存了
        if (params.onlyNet) {
            params.cacheBeforeNet = false
        }

        //返回loading信息
        emit(Resource.loading<ReturnType>())

        val errorInfo = try {

            if (params.cacheBeforeNet) {
                val forceCacheService = createService(RetrofitType.ONLY_CACHE, baseUrl)
                val responseData = call(forceCacheService)
                val result = mapResult(responseData, params)
                if (result.success) {
                    //这是从缓存中读取的数据，先返回让ui显示出来
                    emit(result)
                }

                if (params.onlyCache) {
                    return@liveData
                }
            }

            val service = if (params.onlyNet) {
                //这个只会从网络读取数据
                createService(RetrofitType.ONLY_NET, baseUrl)
            } else {
                //这个会去读取网络数据，如果失败，则读取缓存数据
                createService(RetrofitType.CACHE, baseUrl)
            }

            val responseData = call(service)
            val result = mapResult(responseData, params)
            emit(result)
            return@liveData
        } catch (e: Exception) {
            e.printStackTrace()
            e.message
        }

        MyLog.d(javaClass.name, "fire errorInfo=$errorInfo")
        emit(Resource.failure<ReturnType>(message = errorInfo))
    }

    /**
     * call的请求数据的方法
     */
    suspend inline fun <reified ResultType> Call<ResultType>.callAwait() =
        callAwait(type = ResultType::class.java) { this }

    /**
     * call的请求数据的方法
     * 可以转换请求回来的数据类型，eg：Call<String>.callAwait -> CallResult<Int> ，网络请求返回的是String类型的数据，但是可以转换成Int类型的数据返回回去
     *
     * @param ResponseType 请求数据时返回的数据类型
     * @param ReturnType 真正返回的数据类型
     * @param mapData 通过这个方法可以把 [ResponseType]类型的 的数据转换为 [ReturnType]类型 的数据
     */
    suspend fun <ResponseType, ReturnType> Call<ResponseType>.callAwait(
        type: Class<ResponseType>,
        mapData: ResponseType.() -> ReturnType
    ): CallResult<ReturnType> = withContext(Dispatchers.IO) {

        try {
            val response = execute()
            if (response.code() == 204) {
                //处理delete请求时body为空的情况
                return@withContext CallResult(true, response.code(), null)
            }

            //eg: 用错误的手机号注册时，返回的body为空，response.code==500，但是errorBody中包含后台返回的错误信息
            val body = response.body() ?: try {
                GsonUtils.fromJson(response.errorBody()?.string(), type)
            } catch (e: Exception) {
                null
            }

            if (body == null) {
                val invocation = request().tag(Invocation::class.java)!!
                val method = invocation.method()
                val e =
                    KotlinNullPointerException("Response from " + method.declaringClass.name + '.' + method.name + " was null but response body type was declared as non-null")
                MyLog.e(javaClass.name, "callAwait $e")
                CallResult(false, response.code(), null, e.message ?: "数据请求失败")
            } else {
                CallResult(true, response.code(), mapData(body))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CallResult(false, 0, null, e.message ?: "数据请求失败")
        }
    }
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

    val success: Boolean
        get() = state == ResourceState.Success

    val failure: Boolean
        get() = state == ResourceState.Failure

    val loading: Boolean
        get() = state == ResourceState.Loading

    companion object {

        fun <T> loading() = Resource<T>(ResourceState.Loading, null, code = 0)

        fun <T> failure(message: String? = null) =
            Resource<T>(ResourceState.Failure, null, code = 0, message = message)

        fun <T> success(data: T?, code: Int) =
            Resource(ResourceState.Success, data = data, code = code)
    }
}

inline fun <reified ResultType> Resource<ResultType>.success(
    successBlock: ResultType.() -> Unit
) = apply {
    if (this.success) {
        this.data?.let { successBlock(it) }
    }
}

inline fun <reified ResultType> Resource<ResultType>.complete(
    block: () -> Unit
) = apply {
    if (this.success || this.failure) {
        block()
    }
}

inline fun <reified ResultType> Resource<ResultType>.fail(
    failBlock: Resource<ResultType>.() -> Unit
) = apply {
    if (this.failure) {
        failBlock(this)
    }
}

inline fun <reified ResultType> Resource<ResultType>.loading(
    block: () -> Unit
) = apply {
    if (this.loading) {
        block()
    }
}

data class CallResult<T>(
    val success: Boolean,
    val httpCode: Int,
    val result: T?,
    val message: String = "数据请求失败"
) {
    companion object {

        /**
         * 合并多个CallResult数据
         *
         * eg:一个页面有banner和列表两个接口的数据，刷新这个页面时，需要把两个接口的数据整合成一个Bean返回给页面处理
         * 这个方法就是用来合并多个数据，转成一个CallResult
         *
         * 具体查看：https://github.com/lwcla/ClaWanAndroid/blob/main/home/src/main/java/com/cla/home/vm/HomeRepo.kt
         */
        inline fun <T> join(vararg results: CallResult<*>, block: () -> T): CallResult<T> {

            if (results.find { !it.success } != null) {
                val message = results.find { it.message.isNotBlank() }?.message ?: "数据请求失败"
                return CallResult(false, 0, null, message)
            }

            return CallResult(true, 200, block())
        }
    }
}

/**
 * 请求成功
 */
fun <T> CallResult<T>?.suc() =
    this != null && success && result != null && httpCode / 100 != 4 && httpCode / 100 != 5 && httpCode >= 0

inline fun <T> CallResult<T>.toResource(covert: (T) -> T = { it }) = if (suc()) {
    val data = result?.run { covert(this) }
    Resource.success<T>(data, code = httpCode)
} else {
    Resource.failure<T>(message = message)
}

/**
 * 网络请求的参数
 * @param cacheBeforeNet 在网络请求之前是否需要先读取本地缓存返回，true:先读取本地缓存数据，如果拿到了，那么就先返回缓存数据，然后再去网络读取，如果网络数据拿到了，再返回网络数据（返回来两次）
 * @param onlyCache 是否只读取本地缓存数据，true:读取本地缓存数据，如果本地缓存数据存在的话，就返回，否则就返回空
 * @param onlyNet 是否只读取网络数据,true:只读取网络数据，就算有本地缓存也不管
 * @param baseUrl retrofit的baseUrl
 */
class RequestBuilder<Service, ResponseType, ReturnType>(
    var cacheBeforeNet: Boolean = false,
    var onlyCache: Boolean = false,
    var onlyNet: Boolean = true,
    var baseUrl: String = "",
    var processBean: ReturnType.() -> ReturnType = { this }
)