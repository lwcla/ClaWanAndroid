package com.cla.wan.net.utils

import com.cla.wan.net.config.AddressType
import com.cla.wan.net.config.ServiceAddressEntity
import com.cla.wan.net.config.ServiceAddressHelper
import com.cla.wan.utils.data.MMKVHelper
import com.cla.wan.utils.data.loadBool
import com.cla.wan.utils.data.loadObj
import com.cla.wan.utils.data.saveData

object ServiceAddressUtil {

    private const val SERVICE_ADDRESS_INITIALED = "service_address_initialed"
    private const val SERVICE_ADDRESS_KEY = "service_address_key"

    init {
        ServiceAddressHelper.getServiceAddress().apply {
            // 首次将数据存储再 sp 中
            if (!SERVICE_ADDRESS_INITIALED.loadBool(false)) {
                SERVICE_ADDRESS_INITIALED.saveData(true)
                SERVICE_ADDRESS_KEY.saveData(this)
            }
        }
    }

    fun getAllAddress() = SERVICE_ADDRESS_KEY.loadObj<MutableList<ServiceAddressEntity>>()
        ?: mutableListOf()

    fun getActiveUrl(): String {
        return SERVICE_ADDRESS_KEY.loadObj<MutableList<ServiceAddressEntity>>()?.run {
            return this.find { it.active }?.address ?: ""
        } ?: ""
    }

    fun insertActiveUrl(url: String) {
        val originAddress = SERVICE_ADDRESS_KEY.loadObj<MutableList<ServiceAddressEntity>>()
        originAddress?.let {
            val newAddress =
                it.map { ServiceAddressEntity(it.address, it.type, false) }.toMutableList()
            newAddress.add(ServiceAddressEntity(url, AddressType.OTHER, true))
            MMKVHelper.saveData(SERVICE_ADDRESS_KEY, newAddress)
        }
    }

    fun switchActiveUrl(url: String) {
        val originAddress = SERVICE_ADDRESS_KEY.loadObj<MutableList<ServiceAddressEntity>>()
        originAddress?.let {
            val newAddress = it.map { ServiceAddressEntity(it.address, it.type, url == it.address) }
                .toMutableList()
            MMKVHelper.saveData(SERVICE_ADDRESS_KEY, newAddress)
        }
    }

    fun insertUrl(url: String) {
        val originAddress = SERVICE_ADDRESS_KEY.loadObj<MutableList<ServiceAddressEntity>>()
        originAddress?.let {
            val newAddress =
                it.map { ServiceAddressEntity(it.address, it.type, it.active) }.toMutableList()
            newAddress.add(ServiceAddressEntity(url, AddressType.OTHER, false))
            MMKVHelper.saveData(SERVICE_ADDRESS_KEY, newAddress)
        }
    }

}