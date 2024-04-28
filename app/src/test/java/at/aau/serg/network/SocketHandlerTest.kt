package at.aau.serg.network

import android.util.Log
import at.aau.serg.utils.App
import at.aau.serg.utils.Strings
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class SocketHandlerTest {
    private val mockSocket = mockk<Socket>(relaxed = true)

    @BeforeEach
    fun setUp() {
        mockkStatic(IO::class)
        mockkStatic(Log::class)
        every { IO.socket(any<String>(), any<IO.Options>()) } returns mockSocket
        every { Log.i(any(), any()) } returns 0

        mockkObject(App.Companion)
        val mockApp = mockk<App>()
        every { App.instance } returns mockApp

        mockkStatic(Strings::class)
        every { Strings.get(any()) } returns "http://localhost"
    }

    @Test
    fun `connect should initiate connection and setup basic listeners`() {
        SocketHandler.connect("uuid")

        verify { mockSocket.connect() }
        verify(exactly = 1) { mockSocket.on(eq(Socket.EVENT_CONNECT), any()) }
        verify(exactly = 1) { mockSocket.on(eq(Socket.EVENT_DISCONNECT), any()) }
    }

    @Test
    fun `disconnect should terminate the connection`() {
        SocketHandler.connect("uuid")
        SocketHandler.disconnect()
        verify { mockSocket.disconnect() }
    }

    @Test
    fun `emit should send events with given name and arguments`() {
        SocketHandler.connect("uuid")
        val eventName = "testEvent"
        val args = arrayOf("test1", "test2")

        SocketHandler.emit(eventName, *args)

        verify { mockSocket.emit(eventName, *args) }
    }

    @Test
    fun `event callback should be registered and called`() {
        SocketHandler.connect("uuid")
        val eventName = "custom_event"
        val args = arrayOfNulls<Any>(0)

        SocketHandler.on(eventName) {
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

        SocketHandler.connect("uuid")

        verify { mockSocket.on(eq(Socket.EVENT_CONNECT), capture(connectCaptor)) }
        verify { mockSocket.on(eq(Socket.EVENT_DISCONNECT), capture(disconnectCaptor)) }

        // Simulate triggering the events
        connectCaptor.captured.call()
        disconnectCaptor.captured.call()

        verify(exactly = 1) { Log.i("socket", "Connected") }
        verify(exactly = 1) { Log.i("socket", "Disconnected") }
    }
}