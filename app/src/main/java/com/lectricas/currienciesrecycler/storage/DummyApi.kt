package com.lectricas.currienciesrecycler.storage

import com.lectricas.currienciesrecycler.model.CurrencyModel.Companion.DEFAULT_BASE
import io.reactivex.Single

class DummyApi {

    fun getRates(): Single<CurrencyResponse> {

        val rates = mutableMapOf(
            Pair("AUD", 1.6226),
            Pair("BGN", 1.9633),
            Pair("BRL", 4.8101),
            Pair("CAD", 1.5397)
//            Pair("CHF", 1.1318),
//            Pair("CNY", 7.9754),
//            Pair("CZK", 25.813),
//            Pair("DKK", 7.4851),
//            Pair("GBP", 0.90167),
//            Pair("HKD", 9.1672),
//            Pair("HRK", 7.4625),
//            Pair("HUF", 327.74),
//            Pair("IDR", 17390.0),
//            Pair("ILS", 4.1865),
//            Pair("INR", 84.037),
//            Pair("ISK", 128.29),
//            Pair("JPY", 130.04),
//            Pair("KRW", 1309.7),
//            Pair("MXN", 22.451),
//            Pair("MYR", 4.8304),
//            Pair("NOK", 9.8133),
//            Pair("NZD", 1.77),
//            Pair("PHP", 62.831),
//            Pair("PLN", 4.3348),
//            Pair("RON", 4.6562),
//            Pair("RUB", 79.878),
//            Pair("SEK", 10.631),
//            Pair("SGD", 1.6061),
//            Pair("THB", 38.275),
//            Pair("TRY", 7.6573),
//            Pair("USD", 1.1678),
//            Pair("ZAR", 17.891)
        )

        return Single.just(CurrencyResponse(DEFAULT_BASE, "2018-09-06", rates))
    }
}