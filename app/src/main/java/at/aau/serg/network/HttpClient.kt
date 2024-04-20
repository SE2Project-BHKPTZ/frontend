package at.aau.serg.network

import at.aau.serg.R
import at.aau.serg.utils.Strings
import okhttp3.Callback
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object HttpClient {
    private var client: OkHttpClient = OkHttpClient()
    private var baseUrl: String = Strings.get(R.string.api_url)

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

    fun resetClient(baseUrl: String = Strings.get(R.string.api_url)) {
        client = OkHttpClient()
        this.baseUrl = baseUrl
    }
}