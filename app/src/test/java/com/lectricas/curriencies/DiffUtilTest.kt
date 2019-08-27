package com.lectricas.curriencies

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.lectricas.curriencies.storage.DummyApi
import com.lectricas.curriencies.ui.CurrencyAdapter.CurrenciesDiffUtil
import com.lectricas.curriencies.ui.CurrencyItem
import junit.framework.Assert.assertEquals
import org.junit.*

class DiffUtilTest {

    @Test
    fun test_diff(){

        val oldItems = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6219),
            CurrencyItem("BGN", 1.9625),
            CurrencyItem("BRL", 4.8082),
            CurrencyItem("CAD", 1.5391),
            CurrencyItem("CHF", 1.1314)
        )

        val newItems = listOf(
            CurrencyItem("BRL", 1.0, firstItem = true),
            CurrencyItem("EUR", 3.55),
            CurrencyItem("AUD", 1.6219),
            CurrencyItem("BGN", 1.9625),
            CurrencyItem("CAD", 1.5391),
            CurrencyItem("CHF", 1.1314)
        )


    }
}