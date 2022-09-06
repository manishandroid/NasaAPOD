package com.manish.nasaapod.di

import android.app.Application
import android.content.Context
import com.manish.common_network.api.*
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
    fun provideOkHttpClient() : OkHttpClient {
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
        .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(
            KotlinJsonAdapterFactory()
        ).build()))
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .baseUrl(EndPoint.NASA_BASE_URL)
        .client(okHttpClient)
        .build()


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): APIService = retrofit.create(APIService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelperImpl: ApiHelperImpl): ApiHelper = apiHelperImpl

    @Provides
    @Singleton
    fun provideApiRepository(apiHelperImpl: ApiHelperImpl) = ApiRepository(apiHelperImpl)
}