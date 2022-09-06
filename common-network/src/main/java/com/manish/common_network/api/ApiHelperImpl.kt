package com.manish.common_network.api

import com.manish.common_network.APODResponse
import com.manish.common_network.utils.NetworkResponse
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: APIService) : ApiHelper {

    override suspend fun fetchNasaAPOD(queryMap: HashMap<String, String>): NetworkResponse<APODResponse, Any> {
        return apiService.fetchNasaAPOD(queryMap)
    }
}