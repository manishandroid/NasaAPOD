package com.manish.nasaapod.intent

sealed class MainIntent {

    data class FetchNasaAPOD(val queryMap: HashMap<String, String>) : MainIntent()
}
