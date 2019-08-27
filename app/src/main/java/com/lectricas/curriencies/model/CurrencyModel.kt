package com.lectricas.curriencies.model

import com.lectricas.curriencies.storage.CurrencyApi
import com.lectricas.curriencies.storage.DummyApi
import com.lectricas.curriencies.ui.CurrencyItem
import io.reactivex.Single
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyModel(
    private val api: CurrencyApi,
    private val dummyApi: DummyApi
) {
    fun getRates(number: Int, items: List<CurrencyItem>): Single<List<CurrencyItem>> {
        if (items.isEmpty()) {
            return dummyApi.getRates("1")
                .map { response ->
                    return@map response.rates.map { CurrencyItem(it.key, it.value) }
                }
                .map {
                    val result = mutableListOf(CurrencyItem("1", firstItem = true))
                    result.addAll(it)
                    return@map result
                }
        } else {
            val prepared = items.toMutableList()
            val base = prepared.removeAt(number)
            return dummyApi.getRates(base.id)
                .map { response ->
                    val rates = response.rates
                    return@map prepared.map {
                        CurrencyItem(
                            it.id,
                            rates.getValue(it.id),
                            calculateAmount(base.amount, rates.getValue(it.id))
                        )
                    }
                }
                .map {
                    val result = mutableListOf(CurrencyItem(base.id, amount = base.amount, firstItem = true))
                    result.addAll(it)
                    return@map result
                }
        }
    }

    private fun calculateAmount(amountNow: Double, multiplier: Double): Double {
        return amountNow * multiplier
    }

    fun convert(second: List<CurrencyItem>, amountNow: Double): List<CurrencyItem> {
        return second.map { item ->
            CurrencyItem(
                item.id,
                item.multiplier,
                calculateAmount(amountNow, item.multiplier),
                item.firstItem
            )
        }
    }

    fun validateNumbers(s: String): Double {
        if (s.isBlank()) {
            return 0.0
        }
        val formatter = DecimalFormat()
        val symbol = DecimalFormatSymbols()
        symbol.decimalSeparator = '.'
        formatter.decimalFormatSymbols = symbol
        return formatter.parse(s.replace(",", ".")).toDouble()
    }
}