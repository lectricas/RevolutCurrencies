package com.lectricas.curriencies.storage

import com.lectricas.curriencies.storage.CurrencyResponse
import io.reactivex.Single
import retrofit2.http.GET

interface CurrencyApi {
    @GET("/latest?base=EUR")
    fun getRates(): Single<CurrencyResponse>
}