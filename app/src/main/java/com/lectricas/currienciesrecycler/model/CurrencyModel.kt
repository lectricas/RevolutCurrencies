package com.lectricas.currienciesrecycler.model

import com.lectricas.currienciesrecycler.storage.CurrencyApi
import com.lectricas.currienciesrecycler.ui.CurrencyItem
import io.reactivex.Single
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyModel(
    private val api: CurrencyApi
) {

    companion object {
        const val DEFAULT_BASE = "EUR"
    }

    fun loadRates(items: List<CurrencyItem>): Single<List<CurrencyItem>> {
        return api.getRates(DEFAULT_BASE)
            .map { response ->
                if (items.isEmpty()) {
                    val newItems = response.rates.map { CurrencyItem(it.key, it.value) }.toMutableList()
                    newItems.add(0, CurrencyItem(DEFAULT_BASE, firstItem = true))
                    return@map newItems
                } else {
                    val withBase = response.rates.toMutableMap()
                    withBase[DEFAULT_BASE] = 1.0
                    val newBase = withBase.getValue(items.first().id)
                    val newItems = items.map {
                        val newRate = withBase.getValue(it.id) / newBase
                        CurrencyItem(it.id, newRate, items.first().amount * newRate, it.firstItem)
                    }
                    return@map newItems
                }
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
        val new = listToModify.map { CurrencyItem(it.id, it.rate / base.rate) }.toMutableList()
        new.add(0, CurrencyItem(base.id, firstItem = true))
        return Single.just(convertAmount(new, base.amount))
    }
}