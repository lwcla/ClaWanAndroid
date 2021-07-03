package com.cla.wan.net.entity

import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject

class BodyBuilder {

    companion object {
        fun build(vararg pairs: Pair<String, Any>): RequestBody {
            val jsonObject = JSONObject()

            pairs.forEach {
                try {
                    jsonObject.put(it.first, it.second)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
        }
    }

    private val jsonObject = JSONObject()

    fun put(key: String, value: Any): BodyBuilder {
        try {
            jsonObject.put(key, value)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return this
    }


    fun build(): RequestBody {
        return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
    }
}