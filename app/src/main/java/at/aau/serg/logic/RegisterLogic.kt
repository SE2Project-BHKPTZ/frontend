package at.aau.serg.logic

import android.content.Context
import android.content.Intent
import android.widget.Toast
import at.aau.serg.MainActivity
import at.aau.serg.models.User
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class RegisterLogic(private val context: Context) {
    fun registerUser(username: String, password: String){
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userToRegister = User(username, password)
        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                context.startActivity(Intent(context, MainActivity::class.java))
            }
        }

        HttpClient("http://10.0.2.2:8081").post("users/register", Gson().toJson(userToRegister), null, callback)
    }
}