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
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.Secret
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

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

        val error = Authentication(HttpClient(getString(R.string.api_url))).loginUser(username.toString(), password.toString(), CallbackCreator().createCallback(::onFailureLogin, ::onResponseLogin))
        if(error != null){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onFailureLogin(){
        runOnUiThread{
            Toast.makeText(this@LoginActivity, R.string.loginFailed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onResponseLogin(response: Response){
        if (!response.isSuccessful) {
            runOnUiThread{
                Toast.makeText(this@LoginActivity, R.string.loginFailed, Toast.LENGTH_SHORT).show()
            }
            return
        }

        val responseBody = response.body?.string()
        try{
            StoreToken(ContextWrapper(this@LoginActivity), Secret()).storeTokenFromResponseBody(JSONObject(responseBody))
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