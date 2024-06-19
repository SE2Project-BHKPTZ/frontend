package at.aau.serg.androidutils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import okhttp3.Response
import org.json.JSONObject

object ErrorUtils {
    private var currentToast: Toast? = null

    fun getErrorMessageFromJSONResponse(response: Response, defaultMessage: String?): String {
        val responseString = response.body?.string().orEmpty()

        return if (responseString.isNotEmpty()) {
            JSONObject(responseString).optString("message", defaultMessage)
        } else {
            defaultMessage ?: ""
        }
    }

    fun showToast(context: Context, message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            currentToast?.cancel()
            currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            currentToast?.show()
        }
    }
}