package at.aau.serg.androidutils

import android.content.Context
import android.widget.Toast
import at.aau.serg.R
import okhttp3.Response
import org.json.JSONObject

object ErrorUtils {
    fun getErrorMessageFromJSONResponse(response: Response, defaultMessage: String?): String {
        val responseString = response.body?.string().orEmpty()

        return if (responseString.isNotEmpty()) {
            JSONObject(responseString).optString("message", defaultMessage)
        } else {
            defaultMessage ?: ""
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}