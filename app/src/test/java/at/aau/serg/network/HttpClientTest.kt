package at.aau.serg.network

import okhttp3.Callback
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HttpClientTest {

    private lateinit var server: MockWebServer
    private lateinit var httpClient: HttpClient

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        httpClient = HttpClient()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `POST request should correctly handle jsonBody and authToken`() {
        val jsonBody = """{"key":"value"}"""
        val authToken = "testToken"
        val latch = CountDownLatch(1)

        server.enqueue(MockResponse().setBody("OK"))

        val url = server.url("/").toString()
        httpClient.post(url, jsonBody, authToken, object : Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                latch.countDown()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                assert(response.isSuccessful)
                latch.countDown()
            }
        })

        // Wait for the request to finish
        latch.await(1, TimeUnit.SECONDS)
        val recordedRequest = server.takeRequest()
        assert(recordedRequest.getHeader("Authorization") == "Bearer $authToken")
        assert(recordedRequest.body.readUtf8() == jsonBody)
    }

    @Test
    fun `GET request should correctly handle authToken`() {
        val authToken = "testToken"
        val latch = CountDownLatch(1)

        server.enqueue(MockResponse().setBody("OK"))

        val url = server.url("/").toString()
        httpClient.get(url, authToken, object : Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                latch.countDown()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                assert(response.isSuccessful)
                latch.countDown()
            }
        })

        latch.await(1, TimeUnit.SECONDS)
        val recordedRequest = server.takeRequest()
        assert(recordedRequest.getHeader("Authorization") == "Bearer $authToken")
    }
}