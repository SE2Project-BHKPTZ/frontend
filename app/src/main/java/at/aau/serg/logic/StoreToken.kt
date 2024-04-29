package at.aau.serg.logic

import android.content.ContextWrapper
import org.json.JSONException
import org.json.JSONObject

class StoreToken(private val context: ContextWrapper) {

    fun storeTokens(accessToken: String, refreshToken: String){
        val sharedPreferences = Secret.getSecretSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.putString("refreshToken", refreshToken)
        editor.apply()
    }

    fun storeAccessToken(accessToken: String){
        val sharedPreferences = Secret.getSecretSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", accessToken)
        editor.apply()
    }

    fun storeUUID(uuid: String){
        val sharedPreferences = Secret.getSecretSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.putString("uuid", uuid)
        editor.apply()
    }

    @Throws(JSONException::class)
    fun storeTokenFromResponseBody(jsonObject: JSONObject) {
        val accessToken = jsonObject.getString("accessToken")
        val refreshToken = jsonObject.getString("refreshToken")
        this.storeTokens(accessToken, refreshToken)
    }

    @Throws(JSONException::class)
    fun storeAccessTokenFromBody(jsonObject: JSONObject) {
        val accessToken = jsonObject.getString("accessToken")
        this.storeAccessToken(accessToken)
    }

    fun getAccessToken(): String?{
        val sharedPreferences = Secret.getSecretSharedPref(context)
        return sharedPreferences.getString("accessToken", null)
    }

    fun getRefreshToken(): String?{
        val sharedPreferences = Secret.getSecretSharedPref(context)
        return sharedPreferences.getString("refreshToken", null)
    }

    fun getUUID(): String?{
        val sharedPreferences = Secret.getSecretSharedPref(context)
        return sharedPreferences.getString("uuid", null)
    }

}
