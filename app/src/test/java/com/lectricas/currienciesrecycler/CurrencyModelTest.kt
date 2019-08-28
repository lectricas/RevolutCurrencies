package com.lectricas.currienciesrecycler

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.lectricas.currienciesrecycler.model.CurrencyModel
import com.lectricas.currienciesrecycler.storage.CurrencyApi
import com.lectricas.currienciesrecycler.storage.CurrencyResponse
import com.lectricas.currienciesrecycler.storage.DummyApi
import com.lectricas.currienciesrecycler.ui.CurrencyAdapter.CurrenciesDiffUtil
import com.lectricas.currienciesrecycler.ui.CurrencyItem
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert.assertEquals
import org.junit.*
import org.mockito.Mockito.*
import kotlin.random.Random

class CurrencyModelTest {

    lateinit var currencyModel: CurrencyModel
    private val api: CurrencyApi = mock(CurrencyApi::class.java)
    private val dummyApi = DummyApi()

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        currencyModel = CurrencyModel(api, DummyApi())
    }

    @Test
    @Ignore
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
    @Ignore
    fun testFormatter() {
        print(String.format("%1$,.2f", 0.0))
    }

    @Ignore
    @Test
    fun dummyApiTest() {

        val current = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )

        dummyApi.getRates()
            .map {
                val items = it.rates.map { rates ->
                    CurrencyItem(rates.key, rates.value)
                }.toMutableList()
                items.add(0, CurrencyItem("EUR", firstItem = true))
                return@map items.toList()
            }
            .test()
            .assertValue(current)
    }

    @Test
    fun dummyApiTest1() {

        val EUR = listOf(
            CurrencyItem("EUR", firstItem = true),
            CurrencyItem("AUD", 1.6226),
            CurrencyItem("BGN", 1.9633),
            CurrencyItem("BRL", 4.8101),
            CurrencyItem("CAD", 1.5397)
        )

        val ONE_EUR = currencyModel.convert(EUR, 1.0)

        val EUR_AUD = listOf(
            CurrencyItem("AUD", 1.0, 1.6226, firstItem = true),
            CurrencyItem("EUR", 1.0/1.6226, (1.0/1.6226) * 1.6226),
            CurrencyItem("BGN", 1.9633/1.6226, (1.9633/1.6226) * 1.6226),
            CurrencyItem("BRL", 4.8101/1.6226, (4.8101/1.6226)* 1.6226) ,
            CurrencyItem("CAD", 1.5397/1.6226, (1.5397/1.6226) * 1.6226)
        )
        assertEquals(EUR, currencyModel.getRatesForPicked(0, EUR))
        assertEquals(ONE_EUR, currencyModel.getRatesForPicked(0, ONE_EUR))
        assertEquals(EUR_AUD, currencyModel.getRatesForPicked(1, ONE_EUR))
    }

    @Test
    fun test_convert() {
        val EUR_AUD = listOf(
            CurrencyItem("AUD", 1.0, firstItem = true),
            CurrencyItem("EUR", 1.0/1.6226),
            CurrencyItem("BGN", 1.9633/1.6226),
            CurrencyItem("BRL", 4.8101/1.6226),
            CurrencyItem("CAD", 1.5397/1.6226)
        )

        val EUR_AUD_CONVERTED = listOf(
            CurrencyItem("AUD", 1.0, 250.0, firstItem = true),
            CurrencyItem("EUR", 1.0/1.6226, 1.0/1.6226 * 250),
            CurrencyItem("BGN", 1.9633/1.6226, 1.9633/1.6226 * 250),
            CurrencyItem("BRL", 4.8101/1.6226, 4.8101/1.6226 * 250),
            CurrencyItem("CAD", 1.5397/1.6226, 1.5397/1.6226 * 250)
        )


        assertEquals(EUR_AUD_CONVERTED, currencyModel.convert(EUR_AUD, 225.0))
    }
}