package com.lectricas.currienciesrecycler.ui

import com.lectricas.currienciesrecycler.model.CurrencyModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.dmdev.rxpm.PresentationModel
import java.util.concurrent.TimeUnit.SECONDS

class CurrencyPm(
    private val currencyModel: CurrencyModel
) : PresentationModel() {

    val currenciesState = State(listOf<CurrencyItem>())
    val pickCurrencyAction = Action<Int>()
    val textChangedAction = Action<String>()

    override fun onCreate() {
        super.onCreate()
        pickCurrencyAction.observable
            .flatMapSingle {
                currencyModel.getRates(it, currenciesState.value)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .doOnError {
                //todo
            }
            .retry()
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        textChangedAction.observable
            .map { currencyModel.validateNumbers(it) }
            .map { amountNow ->
                currencyModel.convertAmount(currenciesState.value, amountNow)
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        Observable.interval(1, SECONDS)
            .take(1)
            .flatMapSingle {
                currencyModel.loadRates(currenciesState.value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .doOnError {
                //todo
            }
            .retry()
            .subscribe(currenciesState.consumer)
            .untilDestroy()
    }
}