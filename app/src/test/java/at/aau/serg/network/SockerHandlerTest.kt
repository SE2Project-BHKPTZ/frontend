package at.aau.serg.network

import android.util.Log
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.net.URISyntaxException

class SocketHandlerTest {
    private lateinit var socketHandler: SocketHandler
    private val mockSocket = mockk<Socket>(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockkStatic(IO::class)
        mockkStatic(Log::class)
        every { IO.socket(any<String>()) } returns mockSocket
        every { Log.i(any(), any()) } returns 0

        socketHandler = SocketHandler("http://localhost")
    }

    @Test
    fun `test URISyntaxException is caught in init block`() {
        every { IO.socket(any<String>()) } throws URISyntaxException("", "")

        assertDoesNotThrow { SocketHandler("http123://invalid-url") }

        verify { IO.socket(any<String>()) }
    }

    @Test
    fun `connect should initiate connection and setup basic listeners`() {
        socketHandler.connect()

        verify { mockSocket.connect() }
        verify(exactly = 1) { mockSocket.on(eq(Socket.EVENT_CONNECT), any()) }
        verify(exactly = 1) { mockSocket.on(eq(Socket.EVENT_DISCONNECT), any()) }
    }

    @Test
    fun `disconnect should terminate the connection`() {
        socketHandler.disconnect()
        verify { mockSocket.disconnect() }
    }

    @Test
    fun `emit should send events with given name and arguments`() {
        val eventName = "testEvent"
        val args = arrayOf("test1", "test2")

        socketHandler.emit(eventName, *args)

        verify { mockSocket.emit(eventName, *args) }
    }

    @Test
    fun `event callback should be registered and called`() {
        val eventName = "custom_event"
        val args = arrayOfNulls<Any>(0)

        socketHandler.on(eventName) {
            Log.i("socket", "registered $eventName")
        }

        // Simulate the event being triggered
        val captor = slot<Emitter.Listener>()
        verify { mockSocket.on(eq(eventName), capture(captor)) }
        captor.captured.call(*args)

        verify(exactly = 1) {  Log.i("socket", "registered $eventName") }
    }

    @Test
    fun `setupBasicListeners should register and call connect and disconnect events`() {
        val connectCaptor = slot<Emitter.Listener>()
        val disconnectCaptor = slot<Emitter.Listener>()

        socketHandler.connect()

        verify { mockSocket.on(eq(Socket.EVENT_CONNECT), capture(connectCaptor)) }
        verify { mockSocket.on(eq(Socket.EVENT_DISCONNECT), capture(disconnectCaptor)) }

        // Simulate triggering the events
        connectCaptor.captured.call()
        disconnectCaptor.captured.call()

        verify(exactly = 1) { Log.i("socket", "Connected") }
        verify(exactly = 1) { Log.i("socket", "Disconnected") }
    }
}