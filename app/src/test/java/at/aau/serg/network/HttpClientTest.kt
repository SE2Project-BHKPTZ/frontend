package at.aau.serg.network

import io.mockk.mockk
import okhttp3.Callback
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
        httpClient = HttpClient(server.url("/").toString())
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

        httpClient.post("", jsonBody, authToken, object : Callback {
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
    fun `POST request should work correctly without authToken`() {
        val jsonBody = """{"key":"value"}"""
        val latch = CountDownLatch(1)

        server.enqueue(MockResponse().setBody("OK"))

        httpClient.post("", jsonBody, null, object : Callback {
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
        assert(recordedRequest.getHeader("Authorization") == null)
        assert(recordedRequest.body.readUtf8() == jsonBody)
    }

    @Test
    fun `GET request should correctly handle authToken`() {
        val authToken = "testToken"
        val latch = CountDownLatch(1)

        server.enqueue(MockResponse().setBody("OK"))

        httpClient.get("", authToken, object : Callback {
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

    @Test
    fun `GET request should work correctly without authToken`() {
        val latch = CountDownLatch(1)

        server.enqueue(MockResponse().setBody("OK"))

        httpClient.get("", null, object : Callback {
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
        assert(recordedRequest.getHeader("Authorization") == null)
    }

    @Test
    fun `makeRequestUrl should correctly concatenate URLs`() {
        val makeRequestUrl = httpClient.javaClass.getDeclaredMethod("makeRequestUrl", String::class.java)
        makeRequestUrl.isAccessible = true
        assertEquals("${server.url("/")}test", makeRequestUrl.invoke(httpClient, "test"))
        assertEquals("${server.url("/")}test", makeRequestUrl.invoke(httpClient, "/test"))
    }

    @Test
    fun `makeRequestUrl should throw IllegalArgumentException for invalid URL`() {
        val invalidBaseUrl = "htttp://example.com"
        val client = HttpClient(invalidBaseUrl)
        val callback: Callback = mockk(relaxed = true)

        assertThrows(IllegalArgumentException::class.java) {
            client.get("path", null, callback)
        }
    }
}