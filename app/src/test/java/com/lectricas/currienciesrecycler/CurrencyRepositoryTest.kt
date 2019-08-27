package com.lectricas.currienciesrecycler

import com.google.gson.GsonBuilder
import com.lectricas.currienciesrecycler.storage.CurrencyApi
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
        val inputStream = this.javaClass.classLoader
            .getResourceAsStream("test_response.json")

        val total = IOUtils.toString(inputStream)

        val response = MockResponse()
            .setResponseCode(200)
            .setBody(total)

        mockServer.enqueue(response)

        val observer = api.getRates("EUR").test()
        observer.assertValueCount(1)
            .assertNoErrors()
            .assertValue(Objects.response)
    }
}