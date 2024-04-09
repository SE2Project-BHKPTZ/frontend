package at.aau.serg.logic

import android.content.ContextWrapper
import at.aau.serg.models.User
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.Callback

class Authentication(httpClient: HttpClient) {
    private var httpClient: HttpClient
    init {
        this.httpClient = httpClient
    }
    fun registerUser(username: String, password: String, callback: Callback): String? {
        if(username.isEmpty() || password.isEmpty()){
            return "Username and Password cannot be empty"
        }
        val userToRegister = User(username, password)
        httpClient.post("users/register", Gson().toJson(userToRegister), null, callback)
        return null
    }

    fun loginUser(username: String, password: String, callback: Callback) : String?{
        if(username.isEmpty() || password.isEmpty()){
            return "Username and Password cannot be empty"
        }

        val userToRegister = User(username, password)

        httpClient.post("users/login", Gson().toJson(userToRegister), null, callback)
        return null
    }

    fun tokenValid(context: ContextWrapper, callback: Callback, secret: Secret, storeToken: StoreToken): Boolean{
        val accessToken = storeToken.getAccessToken(context, secret) ?: return false

        httpClient.get("users/me", accessToken, callback)
        return true
    }

    fun updateToken(context: ContextWrapper, callback: Callback, secret: Secret, storeToken: StoreToken): Boolean{
        val refreshToken = storeToken.getRefreshToken(context, secret) ?: return false
        val jsonString = """
            {
            "refreshToken": """" + refreshToken + """"
            }
        """
        httpClient.post("users/refresh", jsonString, null, callback)
        return true
    }

}