package com.lectricas.curriencies.ui

data class CurrencyItem(
    val id: String,
    val multiplier: Double,
    val amount: String = "",
    val firstItem: Boolean = false
) {
    companion object {
        fun newMultiplied(item: CurrencyItem, amount: Double): CurrencyItem {
            return CurrencyItem(
                item.id,
                item.multiplier,
                (item.multiplier * amount).toString(),
                item.firstItem
            )
        }

        fun newUsual(item: CurrencyItem): CurrencyItem {
            return CurrencyItem(
                item.id,
                item.multiplier,
                item.amount,
                false
            )
        }

        fun newFirst(item: CurrencyItem): CurrencyItem {
            return CurrencyItem(
                item.id,
                item.multiplier,
                item.amount,
                true
            )
        }
    }
}