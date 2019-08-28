package com.lectricas.currienciesrecycler.model

import com.lectricas.currienciesrecycler.storage.CurrencyApi
import com.lectricas.currienciesrecycler.storage.DummyApi
import com.lectricas.currienciesrecycler.ui.CurrencyItem
import io.reactivex.Single
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyModel(
    private val api: CurrencyApi,
    private val dummyApi: DummyApi
) {

    companion object {
        const val DEFAULT_BASE = "EUR"
    }
//    fun getRates(number: Int, items: List<CurrencyItem>): Single<List<CurrencyItem>> {
//        if (items.isEmpty()) {
//            return dummyApi.getRates()
//                .map { response ->
//                    return@map response.rates.map { CurrencyItem(it.key, it.value) }
//                }
//                .map {
//                    val result = mutableListOf(CurrencyItem("EUR", firstItem = true))
//                    result.addAll(it)
//                    return@map result
//                }
//        } else {
//            val prepared = items.toMutableList()
//            val base = prepared.removeAt(number)
//            return dummyApi.getRates()
//                .map { response ->
//                    val rates = response.rates
//                    return@map prepared.map {
//                        CurrencyItem(
//                            it.id,
//                            rates.getValue(it.id),
//                            calculateAmount(base.amount, rates.getValue(it.id))
//                        )
//                    }
//                }
//                .map {
//                    val result = mutableListOf(CurrencyItem(base.id, amount = base.amount, firstItem = true))
//                    result.addAll(it)
//                    return@map result
//                }
//        }
//    }

    fun loadRates(): Single<List<CurrencyItem>> {
        return dummyApi.getRates()
            .map { response ->
                return@map response.rates.map { CurrencyItem(it.key, it.value) }
            }
            .map {
                val result = mutableListOf(CurrencyItem(DEFAULT_BASE, firstItem = true))
                result.addAll(it)
                return@map result
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

    fun getRatesForPicked(number: Int, items: List<CurrencyItem>): Single<List<CurrencyItem>> {
        val listToModify = items.toMutableList()
        val base = listToModify.removeAt(number)
        val new = listToModify.map {
            val newMultiplier = it.multiplier / base.multiplier
            CurrencyItem(it.id, newMultiplier, newMultiplier * base.amount)
        }.toMutableList()
        new.add(0, CurrencyItem(base.id, amount = base.amount, firstItem = true))
        return Single.just(new)
    }
}