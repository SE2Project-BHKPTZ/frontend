package at.aau.serg.logic

import at.aau.serg.models.User
import at.aau.serg.network.HttpClient
import com.google.gson.Gson
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

    fun tokenValid(callback: Callback, storeToken: StoreToken): Boolean{
        val accessToken = storeToken.getAccessToken() ?: return false

        httpClient.get("users/me", accessToken, callback)
        return true
    }

    fun updateToken(callback: Callback, storeToken: StoreToken): Boolean{
        val refreshToken = storeToken.getRefreshToken() ?: return false
        val jsonString = """
            {
            "refreshToken": """" + refreshToken + """"
            }
        """
        httpClient.post("users/refresh", jsonString, null, callback)
        return true
    }

}