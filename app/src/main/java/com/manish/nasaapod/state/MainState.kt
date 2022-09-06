package com.manish.nasaapod.state

import com.manish.common_network.APODResponse

sealed class MainState {

    object Idle : MainState()
    object Loading : MainState()
    data class FetchNasaAPODSuccess(val result : APODResponse) : MainState()
    data class FetchNasaAPODApiError(val error : Any) : MainState()
    data class FetchNasaAPODNetworkError(val error : String?) : MainState()
    data class FetchNasaAPODUnknownError(val error : String?) : MainState()
}
