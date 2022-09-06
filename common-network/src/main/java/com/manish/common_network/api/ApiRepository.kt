package com.manish.common_network.api

import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun fetchNasaAPOD(queryMap: HashMap<String, String>) = apiHelper.fetchNasaAPOD(queryMap)
}