package at.aau.serg.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.socket.client.IO
import io.socket.client.Socket
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
        every { IO.socket(any<String>()) } returns mockSocket

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
    fun `on should registers event`() {
        val handler = SocketHandler("http://valid-url")
        val eventName = "test_event"
        handler.on(eventName) {  }

        verify(exactly = 1) { mockSocket.on(eq(eventName), any()) }
    }

    @Test
    fun `emit should send events with given name and arguments`() {
        val eventName = "testEvent"
        val args = arrayOf("test1", "test2")

        socketHandler.emit(eventName, *args)

        verify { mockSocket.emit(eventName, *args) }
    }
}