package com.lectricas.currienciesrecycler

import com.google.gson.GsonBuilder
import com.lectricas.currienciesrecycler.storage.CurrencyApi
import com.lectricas.currienciesrecycler.storage.CurrencyResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.apache.commons.io.IOUtils
import org.junit.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyRepositoryTest() {

    companion object {
        lateinit var api: CurrencyApi
        lateinit var mockServer: MockWebServer

        @BeforeClass
        @JvmStatic
        fun setup() {
            mockServer = MockWebServer()
            mockServer.start()
            api = Retrofit.Builder()
                .baseUrl(mockServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(CurrencyApi::class.java)
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            mockServer.shutdown()
        }
    }

    @Throws(Exception::class)
    @Test
    fun test_response_success() {

        val currencyResponse = CurrencyResponse(
            "EUR",
            "2018-09-06",
            mapOf(
                Pair("AUD", 1.6219),
                Pair("BGN", 1.9625),
                Pair("BRL", 4.8082),
                Pair("CAD", 1.5391),
                Pair("CHF", 1.1314)
            )
        )

        val inputStream = this.javaClass.classLoader
            .getResourceAsStream("test_response.json")

        val total = IOUtils.toString(inputStream)

        val serverResponse = MockResponse()
            .setResponseCode(200)
            .setBody(total)

        mockServer.enqueue(serverResponse)

        val observer = api.getRates("EUR").test()
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertValue(currencyResponse)
    }
}