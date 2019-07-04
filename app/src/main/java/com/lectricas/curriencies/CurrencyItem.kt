package com.lectricas.curriencies

data class CurrencyItem(
    val id: Int,
    val multiplier: Int,
    val amount: String,
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