package at.aau.serg.network

import android.util.Log
import io.socket.client.Socket
import io.socket.client.IO
import java.net.URISyntaxException

class SocketHandler(url: String) {
    private lateinit var socket: Socket

    init {
        try {
            socket = IO.socket(url)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun connect() {
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

    fun setupBasicListeners() {
        on(Socket.EVENT_CONNECT) {
            Log.i("socket", "Connected")
        }

        on(Socket.EVENT_DISCONNECT) {
            Log.i("socket","Disconnected")
        }
    }
}