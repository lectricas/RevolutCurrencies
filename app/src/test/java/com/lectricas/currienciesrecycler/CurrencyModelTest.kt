package com.lectricas.currienciesrecycler

import com.lectricas.currienciesrecycler.model.CurrencyModel
import com.lectricas.currienciesrecycler.storage.CurrencyApi
import com.lectricas.currienciesrecycler.storage.CurrencyResponse
import com.lectricas.currienciesrecycler.ui.CurrencyItem
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.*
import org.mockito.Mockito.*

class CurrencyModelTest {

    lateinit var currencyModel: CurrencyModel
    private val api: CurrencyApi = mock(CurrencyApi::class.java)

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        currencyModel = CurrencyModel(api)
    }

    @Test
    fun test_validate_numbers() {
        assertEquals(currencyModel.validateNumbers(""), 0.0)
        assertEquals(currencyModel.validateNumbers("0"), 0.0)
        assertEquals(currencyModel.validateNumbers("0.0"), 0.0)
        assertEquals(currencyModel.validateNumbers("0,0"), 0.0)
        assertEquals(currencyModel.validateNumbers("0,1"), 0.1)
        assertEquals(currencyModel.validateNumbers("0.1"), 0.1)
        assertEquals(currencyModel.validateNumbers("5"), 5.0)
        assertEquals(currencyModel.validateNumbers("5.5"), 5.5)
    }

    @Test
    fun test_loadRates_emptyList_Success() {

        val CURRENT = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )

        val shortRates = mutableMapOf(
            Pair("AUD", 1.6226),
            Pair("BGN", 1.9633),
            Pair("BRL", 4.8101),
            Pair("CAD", 1.5397)

        )

        val currencyResponse = CurrencyResponse(CurrencyModel.DEFAULT_BASE, "2018-09-06", shortRates)

        `when`(api.getRates(CurrencyModel.DEFAULT_BASE)).thenReturn(Single.just(currencyResponse))

        currencyModel.loadRates(listOf()).test().assertValue(CURRENT)
    }

    @Test
    fun test_loadRates_fullList_Success() {

        val shortRates = mutableMapOf(
            Pair("AUD", 1.4226),
            Pair("BGN", 1.8633),
            Pair("BRL", 4.3101),
            Pair("CAD", 1.4397)

        )

        val currencyResponse = CurrencyResponse(CurrencyModel.DEFAULT_BASE, "2018-09-06", shortRates)
        `when`(api.getRates(CurrencyModel.DEFAULT_BASE)).thenReturn(Single.just(currencyResponse))


        val CURRENT_AUD = listOf(
            CurrencyItem("AUD", 1.0, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226),
            CurrencyItem("BGN", 1.9633 / 1.6226),
            CurrencyItem("BRL", 4.8101 / 1.6226),
            CurrencyItem("CAD", 1.5397 / 1.6226)
        )

        val NEW_AUD = listOf(
            CurrencyItem("AUD", 1.0, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.4226),
            CurrencyItem("BGN", 1.8633 / 1.4226),
            CurrencyItem("BRL", 4.3101 / 1.4226),
            CurrencyItem("CAD", 1.4397 / 1.4226)
        )

        currencyModel.loadRates(CURRENT_AUD).test().assertValue(NEW_AUD)
    }

    @Test
    fun test_loadRates_fullList_with_amount_Success() {

        val shortRates = mutableMapOf(
            Pair("AUD", 1.4226),
            Pair("BGN", 1.8633),
            Pair("BRL", 4.3101),
            Pair("CAD", 1.4397)

        )

        val currencyResponse = CurrencyResponse(CurrencyModel.DEFAULT_BASE, "2018-09-06", shortRates)
        `when`(api.getRates(CurrencyModel.DEFAULT_BASE)).thenReturn(Single.just(currencyResponse))

        val EUR_AUD_CONVERTED_AS_BASE = listOf(
            CurrencyItem("AUD", 1.0, 1.6226, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226, 1.0),
            CurrencyItem("BGN", 1.9633 / 1.6226, 1.9633),
            CurrencyItem("BRL", 4.8101 / 1.6226, 4.8101),
            CurrencyItem("CAD", 1.5397 / 1.6226, 1.5397)
        )

        val NEW_AUD = listOf(
            CurrencyItem("AUD", 1.0, 1.6226, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.4226, 1.0 / 1.4226 * 1.6226),
            CurrencyItem("BGN", 1.8633 / 1.4226, 1.8633 / 1.4226 * 1.6226),
            CurrencyItem("BRL", 4.3101 / 1.4226, 4.3101 / 1.4226 * 1.6226),
            CurrencyItem("CAD", 1.4397 / 1.4226, 1.4397 / 1.4226 * 1.6226)
        )

        currencyModel.loadRates(EUR_AUD_CONVERTED_AS_BASE).test().assertValue(NEW_AUD)
    }


    @Test
    fun test_loadRates_fullList_default_base_Success() {

        val shortRates = mutableMapOf(
            Pair("AUD", 1.4226),
            Pair("BGN", 1.8633),
            Pair("BRL", 4.3101),
            Pair("CAD", 1.4397)

        )

        val currencyResponse = CurrencyResponse(CurrencyModel.DEFAULT_BASE, "2018-09-06", shortRates)
        `when`(api.getRates(CurrencyModel.DEFAULT_BASE)).thenReturn(Single.just(currencyResponse))


        val CURRENT = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )

        val NEW = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.4226),
            CurrencyItem("BGN", 1.8633),
            CurrencyItem("BRL", 4.3101),
            CurrencyItem("CAD", 1.4397)
        )

        currencyModel.loadRates(CURRENT).test().assertValue(NEW)
    }


    @Test
    fun test_convert() {

        val EUR = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )

        val EUR_CONVERTED = listOf(
            CurrencyItem("EUR", amount = 250.0, firstItem = true),
            CurrencyItem("AUD", 1.6226, 1.6226 * 250),
            CurrencyItem("BGN", 1.9633, 1.9633 * 250),
            CurrencyItem("BRL", 4.8101, 4.8101 * 250),
            CurrencyItem("CAD", 1.5397, 1.5397 * 250)
        )

        assertEquals(EUR_CONVERTED, currencyModel.convertAmount(EUR, 250.0))

        val EUR_AUD = listOf(
            CurrencyItem("AUD", 1.0, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226),
            CurrencyItem("BGN", 1.9633 / 1.6226),
            CurrencyItem("BRL", 4.8101 / 1.6226),
            CurrencyItem("CAD", 1.5397 / 1.6226)
        )

        val EUR_AUD_CONVERTED = listOf(
            CurrencyItem("AUD", 1.0, 250.0, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226, 1.0 / 1.6226 * 250),
            CurrencyItem("BGN", 1.9633 / 1.6226, 1.9633 / 1.6226 * 250),
            CurrencyItem("BRL", 4.8101 / 1.6226, 4.8101 / 1.6226 * 250),
            CurrencyItem("CAD", 1.5397 / 1.6226, 1.5397 / 1.6226 * 250)
        )

        assertEquals(EUR_AUD_CONVERTED, currencyModel.convertAmount(EUR_AUD, 250.0))
    }

    @Test
    fun test_getRates() {

        val EUR = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )

        currencyModel.getRates(0, EUR).test().assertValue(EUR)

        val EUR_AUD = listOf(
            CurrencyItem("AUD", 1.0, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226),
            CurrencyItem("BGN", 1.9633 / 1.6226),
            CurrencyItem("BRL", 4.8101 / 1.6226),
            CurrencyItem("CAD", 1.5397 / 1.6226)
        )

        currencyModel.getRates(1, EUR).test().assertValue(EUR_AUD)

        val EUR_CONVERTED = listOf(
            CurrencyItem("EUR", amount = 250.0, firstItem = true),
            CurrencyItem("AUD", 1.6226, 1.6226 * 250),
            CurrencyItem("BGN", 1.9633, 1.9633 * 250),
            CurrencyItem("BRL", 4.8101, 4.8101 * 250),
            CurrencyItem("CAD", 1.5397, 1.5397 * 250)
        )

        val EUR_AUD_CONVERTED = listOf(
            CurrencyItem("AUD", 1.0, 1.6226 * 250, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226, 1.0 / 1.6226 * (1.6226 * 250)),
            CurrencyItem("BGN", 1.9633 / 1.6226, 1.9633 / 1.6226 * (1.6226 * 250)),
            CurrencyItem("BRL", 4.8101 / 1.6226, 4.8101 / 1.6226 * (1.6226 * 250)),
            CurrencyItem("CAD", 1.5397 / 1.6226, 1.5397 / 1.6226 * (1.6226 * 250))
        )

        currencyModel.getRates(1, EUR_CONVERTED).test().assertValue(EUR_AUD_CONVERTED)
    }
}