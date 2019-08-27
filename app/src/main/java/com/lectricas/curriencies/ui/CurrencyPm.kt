package com.lectricas.curriencies.ui

import com.lectricas.curriencies.model.CurrencyModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.PresentationModel
import timber.log.Timber
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.concurrent.TimeUnit.SECONDS

class CurrencyPm(
    private val currencyModel: CurrencyModel
) : PresentationModel() {

    val currenciesState = State<Pair<List<CurrencyItem>, Int>>()
    val pickCurrencyAction = Action<Int>()
    val textChangedAction = Action<String>()

    override fun onCreate() {
        super.onCreate()
        pickCurrencyAction.observable
            .flatMapSingle {
                currencyModel.getRates(currenciesState.valueOrNull, it)
                    .subscribeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        textChangedAction.observable
            .map { validateNumbers(it) }
            .map { amountNow ->
                currencyModel.convert(currenciesState.value.first, amountNow)
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        Observable.interval(1 , SECONDS)
            .take(1)
            .flatMapSingle {
                currencyModel.getRates(currenciesState.valueOrNull, 0)
                    .subscribeOn(AndroidSchedulers.mainThread())
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()
    }

    private fun validateNumbers(s: String): Double {
        if (s.isBlank()) {
            return 0.0
        }
        val formatter = DecimalFormat()
        val symbol = DecimalFormatSymbols()
        symbol.decimalSeparator = '.'
        formatter.decimalFormatSymbols = symbol
        return formatter.parse(s).toDouble()
    }
}