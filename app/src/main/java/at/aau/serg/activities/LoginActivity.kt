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
import at.aau.serg.MainActivity
import at.aau.serg.R
import at.aau.serg.logic.Authentication
import at.aau.serg.logic.Secret
import at.aau.serg.logic.StoreToken
import at.aau.serg.network.HttpClient
import okhttp3.Call
import okhttp3.Callback
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

        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread{
                    Toast.makeText(this@LoginActivity, R.string.LoginFailed, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread{
                        Toast.makeText(this@LoginActivity, R.string.LoginFailed, Toast.LENGTH_SHORT).show()
                    }
                    return
                }

                val responseBody = response.body?.string()
                try{
                    val jsonObject = JSONObject(responseBody)
                    val accessToken = jsonObject.getString("accessToken")
                    val refreshToken = jsonObject.getString("refreshToken")
                    StoreToken().storeTokens(accessToken, refreshToken, ContextWrapper(this@LoginActivity), Secret())

                }catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@LoginActivity, R.string.LoginFailed, Toast.LENGTH_SHORT).show()
                    return
                }

                this@LoginActivity.startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
        }

        val error = Authentication(HttpClient(getString(R.string.api_url))).loginUser(username.toString(), password.toString(), callback)
        if(error != null){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    fun tvNoAccountYetClicked(view: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}