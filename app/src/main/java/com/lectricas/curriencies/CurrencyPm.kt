package com.lectricas.curriencies

import me.dmdev.rxpm.PresentationModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyPm : PresentationModel() {

    val moveToFirstAction = Action<Int>()
    val textChangedAction = Action<String>()
    val currenciesState = State<List<CurrencyItem>>(makeList())

    override fun onCreate() {
        super.onCreate()

        moveToFirstAction.observable
            .map {
                val newItems = currenciesState.value.toMutableList()
                val item = newItems.removeAt(it)
                newItems.add(0, CurrencyItem.newFirst(item))
                newItems[1] = CurrencyItem.newUsual(newItems[1])
                return@map newItems
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        textChangedAction.observable
            .map { validateNumbers(it) }
            .map { amount ->
                currenciesState.value
                    .map { CurrencyItem.newMultiplied(it, amount) }
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()
    }

    private fun makeList(): MutableList<CurrencyItem> {
        val list = mutableListOf(CurrencyItem(0, 1, "0", true))
        list.addAll((1..50).map { CurrencyItem(it, it, "0") })
        return list
    }

    private fun validateNumbers(s: String): Double {
        if (s.isBlank()) {
            return 0.0
        }
        val formatter = DecimalFormat()
        val symbol = DecimalFormatSymbols()
        symbol.decimalSeparator = ','
        formatter.setDecimalFormatSymbols(symbol)
        return formatter.parse(s).toDouble()
    }
}