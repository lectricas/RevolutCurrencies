package com.lectricas.currienciesrecycler

import com.lectricas.currienciesrecycler.model.CurrencyModel
import com.lectricas.currienciesrecycler.storage.CurrencyApi
import com.lectricas.currienciesrecycler.storage.DummyApi
import com.lectricas.currienciesrecycler.ui.CurrencyItem
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.*
import org.mockito.Mockito.*

class CurrencyModelTest {

    lateinit var currencyModel: CurrencyModel
    private val api: CurrencyApi = mock(CurrencyApi::class.java)
    private val dummyApi = DummyApi()

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        currencyModel = CurrencyModel(api, dummyApi)
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
    fun test_loadRates_Success() {

        val current = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )
        currencyModel.loadRates(listOf()).test().assertValue(current)

        val currentNew = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.4226),
            CurrencyItem("BGN", 1.8633),
            CurrencyItem("BRL", 4.9101),
            CurrencyItem("CAD", 1.4397)
        )

        val EUR_AUD = listOf(
            CurrencyItem("AUD", 1.0, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.4226),
            CurrencyItem("BGN", 1.9633 / 1.4226),
            CurrencyItem("BRL", 4.8101 / 1.4226),
            CurrencyItem("CAD", 1.5397 / 1.4226)
        )
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

        val ONE_EUR = currencyModel.convertAmount(EUR, 1.0)

        val EUR_AUD = listOf(
            CurrencyItem("AUD", 1.0, 1.6226, firstItem = true),
            CurrencyItem("EUR", 1.0 / 1.6226, (1.0 / 1.6226) * 1.6226),
            CurrencyItem("BGN", 1.9633 / 1.6226, (1.9633 / 1.6226) * 1.6226),
            CurrencyItem("BRL", 4.8101 / 1.6226, (4.8101 / 1.6226) * 1.6226),
            CurrencyItem("CAD", 1.5397 / 1.6226, (1.5397 / 1.6226) * 1.6226)
        )

        currencyModel.getRates(0, EUR).test().assertValue(EUR)
        currencyModel.getRates(0, ONE_EUR).test().assertValue(ONE_EUR)
        currencyModel.getRates(1, ONE_EUR).test().assertValue(EUR_AUD)
    }
}