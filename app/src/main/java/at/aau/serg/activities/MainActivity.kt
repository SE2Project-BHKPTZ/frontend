package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
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

class MainActivity : AppCompatActivity() {

    private lateinit var httpClient: HttpClient
    private lateinit var authentication: Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        httpClient = HttpClient.getInstance(getString(R.string.api_url))
        authentication = Authentication.getInstance(httpClient)

        if(!authentication.tokenValid(CallbackCreator().createCallback(::startLoginActivity, ::checkIfUserIsAuthenticated), StoreToken(this, Secret()))){
            this.startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun startLoginActivity(){
        this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }

    private fun checkIfUserIsAuthenticated(response: Response){
        if(response.isSuccessful)
            return

        if(response.code != 403){
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }

        if(!authentication.updateToken(CallbackCreator().createCallback(::startLoginActivity, ::checkIfUpdateAccessTokenWorked), StoreToken(this@MainActivity, Secret()))) {
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    private fun checkIfUpdateAccessTokenWorked(response: Response){
        if (!response.isSuccessful) {
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }
        val responseBody = response.body?.string()
        if(responseBody == null){
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }

        try{
            StoreToken(this@MainActivity, Secret()).storeAccessTokenFromBody(JSONObject(responseBody))
        }catch (e: JSONException) {
            e.printStackTrace()
            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            return
        }
    }

    fun btnGoToLoginClicked(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}