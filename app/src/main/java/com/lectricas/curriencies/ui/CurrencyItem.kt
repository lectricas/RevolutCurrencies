package com.lectricas.curriencies.ui

data class CurrencyItem(
    val id: String,
    var multiplier: Double = 1.0,
    var amount: Double = 0.0,
    var firstItem: Boolean = false
)