package at.aau.serg.activities

import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.androidutils.ErrorUtils.getErrorMessageFromJSONResponse
import at.aau.serg.androidutils.ErrorUtils.showToast
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.CallbackCreator
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun btnRegisterClicked(view: View) {
        val username = findViewById<EditText>(R.id.editTextUsername).text.toString()
        val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

        val error = Authentication.registerUser(username, password, CallbackCreator().createCallback(::onFailureRegister, ::onResponseRegister))
        if (error != null) {
            showToast(this, error)
        }
    }

    private fun onFailureRegister(e: IOException) {
        val message = when (e) {
            is java.net.ConnectException -> "Could not connect to the server"
            else -> getString(R.string.registerFailed)
        }
        runOnUiThread {
            showToast(this, message)
        }
    }

    private fun onResponseRegister(response: Response) {
        if (!response.isSuccessful) {
            val errorMessage = getErrorMessageFromJSONResponse(response, getString(R.string.registerFailed))
            runOnUiThread {
                showToast(this, errorMessage)
            }
            return
        }

        val responseBody = response.body?.string()
        if (responseBody == null) {
            runOnUiThread {
                showToast(this, getString(R.string.registerFailed))
            }
            return
        }

        try {
            val jsonObject = JSONObject(responseBody)
            StoreToken(ContextWrapper(this)).storeTokenFromResponseBody(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
            runOnUiThread {
                showToast(this, getString(R.string.registerFailed))
            }
            return
        }

        startActivity(Intent(this, MainActivity::class.java))
    }
    fun tvAlreadyHaveAnAccountClicked(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}