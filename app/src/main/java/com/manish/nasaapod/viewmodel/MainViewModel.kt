package com.manish.nasaapod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.common_network.api.ApiRepository
import com.manish.common_network.utils.NetworkResponse
import com.manish.nasaapod.intent.MainIntent
import com.manish.nasaapod.state.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    val mainIntent = Channel<MainIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<MainState>(MainState.Idle)
    val state : StateFlow<MainState>
    get() = _state

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            mainIntent.consumeAsFlow().collect {
                when(it) {
                    is MainIntent.FetchNasaAPOD -> fetchNasaAPOD(it.queryMap)
                }
            }
        }
    }

    private fun fetchNasaAPOD(queryMap: HashMap<String, String>) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = MainState.Loading
            val response = apiRepository.fetchNasaAPOD(queryMap)
            _state.value = when(response) {
                is NetworkResponse.Success -> {
                    MainState.FetchNasaAPODSuccess(response.body)
                }
                is NetworkResponse.ApiError -> {
                    MainState.FetchNasaAPODApiError(response.body)
                }
                is NetworkResponse.NetworkError -> {
                    MainState.FetchNasaAPODNetworkError(response.error.message)
                }
                is NetworkResponse.UnknownError -> {
                    MainState.FetchNasaAPODUnknownError(response.error?.message)
                }
            }
        }

    }
}