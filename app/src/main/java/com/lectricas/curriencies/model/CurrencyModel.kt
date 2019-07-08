package com.lectricas.curriencies.model

import com.lectricas.curriencies.storage.CurrencyApi
import com.lectricas.curriencies.ui.CurrencyItem
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CurrencyModel(
    private val api: CurrencyApi
) {
    fun getRates(pair: Pair<List<CurrencyItem>, Int>?, number: Int): Single<Pair<List<CurrencyItem>, Int>> {
        var mutable = pair?.first?.toMutableList() ?: mutableListOf()
        val firstItem = if (pair == null) {
            CurrencyItem("EUR", 1.0, 1.0, true)
        } else {
            val first = mutable.removeAt(number)
            CurrencyItem(first.id, 1.0, first.amount, firstItem = true)
        }

        return api.getRates(firstItem.id)
            .map { response ->
                val elements = response.rates
                if (mutable.isEmpty()) {
                    mutable.addAll(elements.toList().map {
                        CurrencyItem(
                            it.first,
                            it.second,
                            getAmount(firstItem.amount, it.second)
                        )
                    })
                } else {
                    mutable = mutable.map { item ->
                        val multiplier = elements[item.id] ?: error("Seems like data is corrupted ")
                        CurrencyItem(
                            item.id,
                            multiplier,
                            getAmount(firstItem.amount, multiplier)
                        )
                    }.toMutableList()
                }
                mutable.add(0, firstItem)
                return@map mutable.toList()
            }
            .map { Pair(it, number) }
            .subscribeOn(Schedulers.io())
    }

    private fun getAmount(amountNow: Double, multiplier: Double): Double {
        return amountNow * multiplier
    }

    fun convert(second: List<CurrencyItem>, amountNow: Double): Pair<List<CurrencyItem>, Int> {
        return Pair(
            second.map { item ->
                CurrencyItem(
                    item.id,
                    item.multiplier,
                    getAmount(amountNow, item.multiplier),
                    item.firstItem
                )
            },
            0
        )
    }
}