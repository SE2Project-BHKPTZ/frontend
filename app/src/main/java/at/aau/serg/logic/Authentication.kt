package at.aau.serg.logic

import at.aau.serg.models.User
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import okhttp3.Callback

class Authentication() {
    fun registerUser(username: String, password: String, callback: Callback): String? {
        if(username.isEmpty() || password.isEmpty()){
            return "Please fill all fields"
        }

        val userToRegister = User(username, password)
        HttpClient("http://10.0.2.2:8081").post("users/register", Gson().toJson(userToRegister), null, callback)
        return null
    }

    fun loginUser(username: String, password: String, callback: Callback) : String?{
        if(username.isEmpty() || password.isEmpty()){
            return "Please fill all fields"
        }

        val userToRegister = User(username, password)

        HttpClient("http://10.0.2.2:8081").post("users/login", Gson().toJson(userToRegister), null, callback)
        return null
    }
}