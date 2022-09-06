package com.manish.common_network.api

import com.manish.common_network.utils.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface APIService {

    @GET(EndPoint.NASA_APOD)
    suspend fun fetchNasaAPOD(@QueryMap queryMap: HashMap<String, String>) : NetworkResponse<Any, Any>
}