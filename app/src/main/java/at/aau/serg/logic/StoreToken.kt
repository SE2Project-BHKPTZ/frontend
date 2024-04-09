package at.aau.serg.logic

import android.content.Context
import android.content.ContextWrapper
import org.json.JSONException
import org.json.JSONObject

class StoreToken {
    fun storeTokens(accessToken: String, refreshToken: String, context: ContextWrapper, secret: Secret){
        val sharedPreferences =secret.getSecretSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    fun storeAccessToken(accessToken: String, context: ContextWrapper, secret: Secret){
        val sharedPreferences =secret.getSecretSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.apply()
    }

    @Throws(JSONException::class)
    fun storeTokenFromResponseBody(responseBody: String?, context: ContextWrapper) {
        val jsonObject = JSONObject(responseBody)
        val accessToken = jsonObject.getString("accessToken")
        val refreshToken = jsonObject.getString("refreshToken")
        StoreToken().storeTokens(accessToken, refreshToken, context, Secret())
    }

    @Throws(JSONException::class)
    fun storeAccessTokenFromBody(responseBody: String?, context: ContextWrapper) {
        val jsonObject = JSONObject(responseBody)
        val accessToken = jsonObject.getString("accessToken")
        StoreToken().storeAccessToken(accessToken, context, Secret())
    }

    fun getAccessToken(context: Context, secret: Secret): String?{
        val sharedPreferences = secret.getSecretSharedPref(context)
        return sharedPreferences.getString("accessToken", null)
    }

    fun getRefreshToken(context: Context, secret: Secret): String?{
        val sharedPreferences = secret.getSecretSharedPref(context)
        return sharedPreferences.getString("refreshToken", null)
    }

}