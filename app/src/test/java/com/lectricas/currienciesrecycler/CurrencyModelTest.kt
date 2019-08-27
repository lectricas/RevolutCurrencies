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

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        currencyModel = CurrencyModel(api, DummyApi())
    }

    @Test
    @Ignore
    fun test_getRates_initial_success() {

        `when`(api.getRates("EUR"))
            .thenReturn(Single.just(Objects.response))

        val testObserver1 = currencyModel.getRates(0, listOf()).test()

        verify(api).getRates("EUR")

        testObserver1
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue(Objects.currencyItems)
    }

    @Test
    @Ignore
    fun test_getRates_already_success() {
        val initialObjects = Objects.currencyItems.toMutableList()
        initialObjects.shuffle()
        val picked = Random.nextInt(initialObjects.size)

        val newMap = Objects.response.rates.toMutableMap()
        newMap["EUR"] = 1.0
        newMap.remove(initialObjects[picked].id)

        val newResponse = CurrencyResponse(initialObjects[picked].id, Objects.response.date, newMap)
        `when`(api.getRates(initialObjects[picked].id))
            .thenReturn(Single.just(newResponse))

        val testObserver1 = currencyModel.getRates(picked, initialObjects).test()

        verify(api).getRates(initialObjects[picked].id)

        val expectedObjects = initialObjects.map {
            CurrencyItem(it.id, it.multiplier)
        }.toMutableList()
        val toInsert = expectedObjects.removeAt(picked)
        expectedObjects.add(0, CurrencyItem(toInsert.id, 1.0, firstItem = true))

        testObserver1
            .assertNoErrors()
            .assertValueCount(1)
            .assertValue(expectedObjects)

        val diffResult = DiffUtil.calculateDiff(CurrenciesDiffUtil(initialObjects, expectedObjects))

        diffResult.dispatchUpdatesTo(object: ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                println("changed position $position count $count")
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                println("moved from $fromPosition to $toPosition")
                assertEquals(fromPosition, picked)
                assertEquals(toPosition, 0)
            }

            override fun onInserted(position: Int, count: Int) {
                println("inserted position $position count $count")
            }

            override fun onRemoved(position: Int, count: Int) {
                println("removed position $position count $count")
            }
        })
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

    @Ignore
    @Test
    fun test_convert() {
        fun newItems(amountNow: Double): List<CurrencyItem> {
            return Objects.currencyItems
                .toMutableList()
                .map {
                    CurrencyItem(
                        it.id, it.multiplier, it.multiplier * amountNow, it.firstItem
                    )
                }
        }

        val diffResult = DiffUtil.calculateDiff(CurrenciesDiffUtil(Objects.currencyItems, newItems(5.2)))

        diffResult.dispatchUpdatesTo(object: ListUpdateCallback {
            override fun onChanged(position: Int, count: Int, payload: Any?) {
                print("changed items from $position howManyItems $count")
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                print("moved from $fromPosition to $toPosition")
            }

            override fun onInserted(position: Int, count: Int) {
                print("inserted position $position count $count")
            }

            override fun onRemoved(position: Int, count: Int) {
                print("removed position $position count $count")
            }
        })

//        assertEquals(currencyModel.convert(Objects.currencyItems, 0.0), newItems(0.0))
//        assertEquals(currencyModel.convert(Objects.currencyItems, 0.1), newItems(0.1))
//        assertEquals(currencyModel.convert(Objects.currencyItems, 5.2), newItems(5.2))
//        assertEquals(currencyModel.convert(Objects.currencyItems, 12523.3324), newItems(12523.3324))
    }

    @Test
    fun testFormatter() {
        print(String.format("%1$,.2f", 0.0))
    }
}