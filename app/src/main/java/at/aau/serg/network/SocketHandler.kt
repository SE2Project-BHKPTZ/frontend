package at.aau.serg.network

import android.util.Log
import at.aau.serg.R
import at.aau.serg.utils.Strings
import io.socket.client.Socket
import io.socket.client.IO
import java.net.URISyntaxException

object SocketHandler {
    private lateinit var socket: Socket
    fun connect(uuid: String) {
        if (::socket.isInitialized && socket.connected()) {
            return
        }

        try {
            val options: IO.Options = IO.Options()
            options.query = "uuid=$uuid"
            socket = IO.socket(Strings.get(R.string.api_url),options)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        socket.connect()
        setupBasicListeners()
    }

    fun disconnect() {
        socket.disconnect()
    }

    fun emit(eventName: String, vararg args: Any) {
        socket.emit(eventName, *args)
    }

    fun on(eventName: String, callback: (Array<Any>) -> Unit) {
        socket.on(eventName) { args ->
            callback(args)
        }
    }

    fun off(eventName: String) {
        socket.off(eventName)
    }

    fun setupBasicListeners() {
        on(Socket.EVENT_CONNECT) {
            Log.i("socket", "Connected")
        }

        on(Socket.EVENT_DISCONNECT) {
            Log.i("socket","Disconnected")
        }
    }
}