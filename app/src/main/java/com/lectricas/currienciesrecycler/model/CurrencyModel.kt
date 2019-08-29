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

    fun loadRates(items: List<CurrencyItem>): Single<List<CurrencyItem>> {
        return dummyApi.getRates()
            .map { response ->
                if (items.isEmpty()) {
                    return@map response.rates.map { CurrencyItem(it.key, it.value) }
                } else {
                    items.map {
                        val rate = response.rates.getValue(it.id)
                        CurrencyItem(it.id, rate, items.first().amount * rate)

                    }
                }
                    return@map response.rates.map { CurrencyItem(it.key, it.value) }
            }
            .map {
                val result = mutableListOf(CurrencyItem(DEFAULT_BASE, firstItem = true))
                result.addAll(it)
                return@map result
            }
    }

    fun convertAmount(items: List<CurrencyItem>, amountNow: Double): List<CurrencyItem> {
        return items.map { item ->
            CurrencyItem(
                item.id,
                item.rate,
                item.rate * amountNow,
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

    fun getRates(number: Int, items: List<CurrencyItem>): Single<List<CurrencyItem>> {
        val listToModify = items.toMutableList()
        val base = listToModify.removeAt(number)
        val new = listToModify.map {
            val newMultiplier = it.rate / base.rate
            CurrencyItem(it.id, newMultiplier, newMultiplier * base.amount)
        }.toMutableList()
        new.add(0, CurrencyItem(base.id, amount = base.amount, firstItem = true))
        return Single.just(new)
    }
}