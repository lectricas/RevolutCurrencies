package com.lectricas.curriencies

import android.app.Application
import android.content.Context
import com.google.gson.GsonBuilder
import com.lectricas.curriencies.storage.CurrencyApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.util.concurrent.TimeUnit
import okhttp3.logging.HttpLoggingInterceptor



class App : Application() {

    lateinit var serverApi: CurrencyApi

    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
        serverApi = createServerApi(createOkHttpClient())
    }

    private fun createServerApi(okHttpClient: OkHttpClient): CurrencyApi {
        return Retrofit.Builder()
            .baseUrl("https://revolut.duckdns.org")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)

        }.build()
    }
}