package com.lectricas.currienciesrecycler.ui

import com.lectricas.currienciesrecycler.model.CurrencyModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.bindProgress
import me.dmdev.rxpm.skipWhileInProgress
import timber.log.Timber
import java.util.concurrent.TimeUnit.SECONDS

class CurrencyPm(
    private val currencyModel: CurrencyModel
) : PresentationModel() {

    val currenciesState = State(listOf<CurrencyItem>())
    val currencyProgress = State(false)
    val errorState = State(false)
    private val loadingProgress = State(false)

    val errorCommand = Command<Unit>()
    val pickCurrencyAction = Action<Int>()
    val textChangedAction = Action<String>()
    val tryAgain = Action<Unit>()

    override fun onCreate() {
        super.onCreate()

        pickCurrencyAction.observable
            .flatMapSingle {
                currencyModel.getRates(it, currenciesState.value)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
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

        tryAgain.observable
            .doOnNext {
                errorState.consumer.accept(false)
            }
            .switchMap { Observable.interval(0,1, SECONDS) }
            .skipWhileInProgress(loadingProgress.observable)
            .flatMapSingle {
                currencyModel.loadRates(currenciesState.value)
                    .bindProgress(loadingProgress.consumer)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
            .doOnError {
                if (currenciesState.value.isEmpty()) {
                    errorState.consumer.accept(true)
                } else {
                    errorCommand.consumer.accept(Unit)
                }
            }
            .retry()
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        loadingProgress.observable
            .filter { currenciesState.value.isEmpty() }
            .subscribe(currencyProgress.consumer)

        tryAgain.consumer.accept(Unit)
    }
}