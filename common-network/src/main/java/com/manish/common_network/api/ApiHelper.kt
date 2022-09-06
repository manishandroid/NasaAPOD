package com.manish.common_network.api

import com.manish.common_network.utils.NetworkResponse

interface ApiHelper {
    suspend fun fetchNasaAPOD(queryMap: HashMap<String, String>) : NetworkResponse<Any, Any>
}