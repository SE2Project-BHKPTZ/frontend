package at.aau.serg.network

import io.mockk.mockk
import okhttp3.Call
import okhttp3.Response
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class CallbackCreatorTest {
    private lateinit var callbackCreator: CallbackCreator
    private lateinit var mockCall: Call
    private lateinit var mockResponse: Response
    private lateinit var mockIOException: IOException

    @BeforeEach
    fun setUp() {
        callbackCreator = CallbackCreator()
        mockCall = mockk()
        mockResponse = mockk()
        mockIOException = mockk()
    }

    @Test
    fun testOnFailure() {
        var failureCalled = false
        val onFailure: (e: IOException) -> Unit = { e ->
            failureCalled = true
            assert(e === mockIOException)
        }

        val callback = callbackCreator.createCallback(onFailure, {})
        callback.onFailure(mockCall, mockIOException)

        assert(failureCalled)
    }

    @Test
    fun testOnResponse() {
        var responseCalled = false
        val onResponse: (response: Response) -> Unit = { response ->
            responseCalled = true
            assert(response === mockResponse)
        }

        val callback = callbackCreator.createCallback({}, onResponse)
        callback.onResponse(mockCall, mockResponse)

        assert(responseCalled)
    }
}