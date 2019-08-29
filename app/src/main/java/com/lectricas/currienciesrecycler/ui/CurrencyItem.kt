package com.lectricas.currienciesrecycler.ui

data class CurrencyItem(
    val id: String,
    var rate: Double = 1.0,
    var amount: Double = 0.0,
    var firstItem: Boolean = false
)