package com.lectricas.curriencies

import com.lectricas.curriencies.storage.CurrencyResponse
import com.lectricas.curriencies.ui.CurrencyItem

class Objects {
    companion object {
        val response = CurrencyResponse(
            "EUR",
            "2018-09-06",
            mapOf(
                Pair("AUD", 1.6219),
                Pair("BGN", 1.9625),
                Pair("BRL", 4.8082),
                Pair("CAD", 1.5391),
                Pair("CHF", 1.1314)
            )
        )

        val currencyItems = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6219),
            CurrencyItem("BGN", 1.9625),
            CurrencyItem("BRL", 4.8082),
            CurrencyItem("CAD", 1.5391),
            CurrencyItem("CHF", 1.1314)
        )
    }
}
