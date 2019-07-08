package com.lectricas.curriencies.ui

data class CurrencyItem(
    val id: String,
    var multiplier: Double,
    var amount: Double = 0.0,
    var firstItem: Boolean = false
)