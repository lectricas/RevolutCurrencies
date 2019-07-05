package com.lectricas.curriencies.model

import com.lectricas.curriencies.storage.CurrencyApi
import com.lectricas.curriencies.ui.CurrencyItem
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class CurrencyModel(
    private val api: CurrencyApi
) {
    fun getRates(): Single<List<CurrencyItem>> {
        return api.getRates()
            .doOnSuccess { Timber.d(it.toString()) }
            .map {response ->
                response.rates.toList().map { CurrencyItem(it.first, it.second) }
            }
            .subscribeOn(Schedulers.io())
    }
}