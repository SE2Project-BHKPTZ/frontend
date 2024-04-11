package at.aau.serg.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class CallbackCreator {

    fun createCallback(onFailure: (call: Call, e: IOException) -> Unit, onResponse: (call: Call, response: Response) -> Unit):Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                onResponse(call, response)
            }
        }
    }
}