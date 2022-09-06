package com.manish.nasaapod.di

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import com.manish.common_network.api.EndPoint
import com.manish.common_network.utils.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    companion object {
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_WRITE_TIMEOUT = 60L

    }

    @Provides
    @Singleton
    fun provideContext(application: Application) : Context {
        return  application
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(application: Application) : OkHttpClient {
        val okhttp = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(READ_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor { Timber.tag("OkHttp").d(it) }.setLevel(HttpLoggingInterceptor.Level.BODY))

        return okhttp.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().addLast(
            KotlinJsonAdapterFactory()
        ).build()))
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .baseUrl(EndPoint.NASA_BASE_URL)
        .client(okHttpClient)
        .build()


}