package at.aau.serg.activities

import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import at.aau.serg.R
import at.aau.serg.androidutils.ErrorUtils.getErrorMessageFromJSONResponse
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.CallbackCreator
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun btnLoginClicked(view: View) {
        val username = findViewById<EditText>(R.id.editTextUsername).text
        val password = findViewById<EditText>(R.id.editTextPassword).text

        val error = Authentication.loginUser(username.toString(), password.toString(), CallbackCreator().createCallback(::onFailureLogin, ::onResponseLogin))
        if(error != null){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onFailureLogin(e: IOException){
        when (e) {
            is java.net.ConnectException -> {
                runOnUiThread{
                    Toast.makeText(this@LoginActivity, "Could not connect to the server", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                runOnUiThread{
                    Toast.makeText(this@LoginActivity, R.string.loginFailed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onResponseLogin(response: Response){
        if (!response.isSuccessful) {
            val errorMessage = getErrorMessageFromJSONResponse(response, getString(R.string.loginFailed))

            runOnUiThread{
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
            return
        }

        val responseBody = response.body?.string()
        try{
            StoreToken(ContextWrapper(this@LoginActivity)).storeTokenFromResponseBody(JSONObject(responseBody))
        }catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this@LoginActivity, R.string.loginFailed, Toast.LENGTH_SHORT).show()
            return
        }

        this@LoginActivity.startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    fun tvNoAccountYetClicked(view: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}