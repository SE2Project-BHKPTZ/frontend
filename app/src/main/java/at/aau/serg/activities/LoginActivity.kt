package at.aau.serg.activities

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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
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

    fun btnLogin_Clicked(view: View) {
        val username = findViewById<EditText>(R.id.editTextUsername).text
        val password = findViewById<EditText>(R.id.editTextPassword).text

        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    return
                }
                this@LoginActivity.startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
        }

        Authentication().loginUser(username.toString(), password.toString(), callback)
    }

    fun tvNoAccountYet_Clicked(view: View){
        startActivity(Intent(this, RegisterActivity::class.java))
    }
}