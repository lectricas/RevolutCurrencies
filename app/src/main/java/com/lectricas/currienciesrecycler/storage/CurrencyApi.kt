package com.lectricas.currienciesrecycler.storage

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("/latest")
    fun getRates(@Query("base") base: String): Single<CurrencyResponse>
}