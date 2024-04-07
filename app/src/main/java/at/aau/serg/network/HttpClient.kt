package at.aau.serg.network

import android.content.res.Resources
import androidx.core.content.ContextCompat.getString
import at.aau.serg.R
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Properties

class HttpClient(private var baseUrl: String) {
    private var client: OkHttpClient = OkHttpClient()

    fun post(url: String, jsonBody: String, authToken: String?, callback: Callback) {
        val body = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val requestUrl = makeRequestUrl(url)
        val request = Request.Builder()
            .url(requestUrl)
            .post(body)
            .apply {
                authToken?.let { addHeader("Authorization", "Bearer $it") }
            }
            .build()

        client.newCall(request).enqueue(callback)
    }

    fun get(url: String, authToken: String?, callback: Callback) {
        val requestUrl = makeRequestUrl(url)
        val request = Request.Builder()
            .url(requestUrl)
            .apply {
                authToken?.let { addHeader("Authorization", "Bearer $it") }
            }
            .build()

        client.newCall(request).enqueue(callback)
    }

    @Throws(IllegalArgumentException::class)
    private fun makeRequestUrl(path: String): String {
        return baseUrl.toHttpUrl().newBuilder().addPathSegments(path.trimStart('/')).build().toString()
    }
}