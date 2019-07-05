package com.lectricas.curriencies.storage

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)