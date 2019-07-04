package com.lectricas.curriencies

import me.dmdev.rxpm.PresentationModel

class CurrencyPm : PresentationModel() {

    val moveToFirstAction = Action<CurrencyItem>()
    val textChangedAction = Action<Int>()
    val currenciesState = State<List<CurrencyItem>>(makeList())

    override fun onCreate() {
        super.onCreate()

        moveToFirstAction.observable
            .map {
                val newItems = currenciesState.value.toMutableList()
                newItems.remove(it)
                newItems.add(0, CurrencyItem.newFirst(it))
                newItems[1] = CurrencyItem.newUsual(newItems[1])
                return@map newItems
            }
            .subscribe(currenciesState.consumer)
            .untilDestroy()

        textChangedAction.observable
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
}