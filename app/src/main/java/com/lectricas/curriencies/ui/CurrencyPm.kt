package com.lectricas.curriencies.ui

import com.lectricas.curriencies.model.CurrencyModel
import io.reactivex.android.schedulers.AndroidSchedulers
import me.dmdev.rxpm.PresentationModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyPm(
    private val currencyModel: CurrencyModel
) : PresentationModel() {

    val moveToFirstAction = Action<Int>()
    val textChangedAction = Action<String>()
    val currenciesState = State<Pair<Boolean, List<CurrencyItem>>>(Pair(true, makeList()))

    override fun onCreate() {
        super.onCreate()

        currencyModel.getRates()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
            .untilDestroy()

        moveToFirstAction.observable
            .map {
                val newItems = currenciesState.value.second.toMutableList()
                val item = newItems.removeAt(it)
                newItems.add(0, CurrencyItem.newFirst(item))
                newItems[1] = CurrencyItem.newUsual(newItems[1])
                return@map newItems
            }
            .map { Pair(true, it) }
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        textChangedAction.observable
            .map { validateNumbers(it) }
            .map { amount ->
                currenciesState.value.second
                    .map { CurrencyItem.newMultiplied(it, amount) }
            }
            .map { Pair(false, it) }
            .subscribe(currenciesState.consumer)
            .untilDestroy()
    }

    private fun makeList(): MutableList<CurrencyItem> {
        val list = mutableListOf(CurrencyItem("0", 1.0, "", true))
        list.addAll((1..50).map { CurrencyItem(it.toString(), it.toDouble(), "") })
        return list
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