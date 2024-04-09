package at.aau.serg.activities

import android.content.ContextWrapper
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
import at.aau.serg.network.HttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val getTokenCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    if(response.code != 403){
                        this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        return
                    }

                    val refreshTokenCallback = object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        }

                        override fun onResponse(call: Call, response: Response) {
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
                                StoreToken().storeAccessTokenFromBody(responseBody, this@MainActivity)
                            }catch (e: JSONException) {
                                e.printStackTrace()
                                this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                return
                            }
                        }
                    }
                    if(!Authentication(HttpClient(getString(R.string.api_url))).updateToken(this@MainActivity,refreshTokenCallback, Secret(), StoreToken())) {
                        this@MainActivity.startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                }
            }
        }

        if(!Authentication(HttpClient(getString(R.string.api_url))).tokenValid(this, getTokenCallback, Secret(), StoreToken())){
            this.startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun btnGoToLoginClicked(view: View){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}