package com.lectricas.curriencies.storage

import io.reactivex.Single

class DummyApi {

    fun getRates(base: String): Single<CurrencyResponse> {

        val rates = mutableMapOf(
            Pair("1", 1.6219),
            Pair("2", 1.9625),
            Pair("3", 4.8082),
            Pair("4", 1.5391),
            Pair("5", 1.1314),
            Pair("6", 1.1314),
            Pair("7", 1.1314),
            Pair("8", 1.1314),
            Pair("9", 1.1314),
            Pair("10", 1.1314),
            Pair("11", 1.1314),
            Pair("12", 1.1314),
            Pair("13", 1.1314),
            Pair("14", 1.1314),
            Pair("15", 1.1314),
            Pair("16", 1.1314),
            Pair("17", 1.1314),
            Pair("18", 1.1314),
            Pair("19", 1.1314),
            Pair("20", 1.1314),
            Pair("21", 1.1314),
            Pair("22", 1.1314),
            Pair("23", 1.1314),
            Pair("24", 1.1314),
            Pair("25", 1.1314),
            Pair("26", 1.1314),
            Pair("27", 1.1314),
            Pair("28", 1.1314),
            Pair("29", 1.1314),
            Pair("30", 1.1314)
        )

        rates.remove(base)

        return Single.just(CurrencyResponse(base, "2018-09-06", rates))
    }
}