package com.manish.common_network.utils

import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException
import java.lang.UnsupportedOperationException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal class NetworkResponseCall<S: Any, E: Any>(
    private val delegate: Call<S>,
    private val errorConverter: Converter<ResponseBody, E>,
) : Call<NetworkResponse<S, E>>{

    override fun enqueue(callback: Callback<NetworkResponse<S, E>>) {
        return delegate.enqueue(object: Callback<S> {
            override fun onResponse(call: Call<S>, response: Response<S>) {
                val body = response.body()
                val code = response.code()
                val error = response.errorBody()

                if(response.isSuccessful) {
                    if(body != null) {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.Success(body)))
                    } else {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.UnknownError(null)))
                    }
                } else {
                    val errorBody = when {
                        error == null -> null
                        error.contentLength() == 0L -> null
                        else -> try {
                            errorConverter.convert(error)
                        } catch (e: Exception) { null }
                    }

                    if(errorBody != null) {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.ApiError(errorBody, code)))
                    } else {
                        callback.onResponse(this@NetworkResponseCall, Response.success(NetworkResponse.UnknownError(null)))
                    }
                }
            }

            override fun onFailure(call: Call<S>, throwable: Throwable) {
                throwable.printStackTrace()
                val networkResponse = when(throwable) {
                    is UnknownHostException -> NetworkResponse.NetworkError(IOException("Socket Timeout"))
                    is SocketTimeoutException -> NetworkResponse.NetworkError(IOException("Socket Timeout"))
                    is SocketException -> NetworkResponse.NetworkError(IOException("Socket Exception"))
                    is IOException -> NetworkResponse.NetworkError(throwable)
                    else -> NetworkResponse.UnknownError(throwable)
                }
            }

        })
    }

    override fun isExecuted(): Boolean  = delegate.isExecuted

    override fun clone(): Call<NetworkResponse<S, E>> = NetworkResponseCall(delegate.clone(), errorConverter)

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun cancel()  = delegate.cancel()

    override fun execute(): Response<NetworkResponse<S, E>> {
        throw UnsupportedOperationException("NetworkResponseCall doesn't support execute")
    }

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}