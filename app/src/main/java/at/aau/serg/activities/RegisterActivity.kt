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
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.CallbackCreator
import at.aau.serg.network.HttpClient
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private lateinit var httpClient: HttpClient
    private lateinit var authentication: Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        httpClient = HttpClient.getInstance(getString(R.string.api_url))
        authentication = Authentication.getInstance(httpClient)
    }

    fun btnRegisterClicked(view: View) {
        val username = findViewById<EditText>(R.id.editTextUsername).text
        val password = findViewById<EditText>(R.id.editTextPassword).text

        val error = authentication.registerUser(username.toString(), password.toString(), CallbackCreator().createCallback(::onFailureRegister, ::onResponseRegister))
        if(error != null){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onFailureRegister(){
        runOnUiThread{
            Toast.makeText(this@RegisterActivity, R.string.registerFailed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onResponseRegister( response: Response){
        if (!response.isSuccessful) {
            runOnUiThread{
                Toast.makeText(this@RegisterActivity, R.string.registerFailed, Toast.LENGTH_SHORT).show()
            }
            return
        }
        val responseBody = response.body?.string()
        try{
            StoreToken(ContextWrapper(this@RegisterActivity)).storeTokenFromResponseBody(JSONObject(responseBody))
        }catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this@RegisterActivity, "Registration success", Toast.LENGTH_SHORT).show()
            this@RegisterActivity.startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            return
        }
        this@RegisterActivity.startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
    }

    fun tvAlreadyHaveAnAccountClicked(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}