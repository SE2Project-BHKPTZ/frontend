package at.aau.serg.network

import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HttpClient {
    private var client: OkHttpClient = OkHttpClient()

    fun post(url: String, jsonBody: String, authToken: String? = null, callback: Callback) {
        val body = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .apply {
                authToken?.let { addHeader("Authorization", "Bearer $it") }
            }
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun get(url: String, authToken: String? = null, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .apply {
                authToken?.let { addHeader("Authorization", "Bearer $it") }
            }
            .build()

        client.newCall(request).enqueue(callback)
    }
}