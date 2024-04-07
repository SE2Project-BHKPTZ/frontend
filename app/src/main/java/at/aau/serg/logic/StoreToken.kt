package at.aau.serg.logic

import android.content.ContextWrapper
import org.json.JSONException

class StoreToken {
    fun storeTokens(accessToken: String, refreshToken: String, context: ContextWrapper, secret: Secret){
        try{
            val sharedPreferences =secret.getSecretSharedPref(context)
            val editor = sharedPreferences.edit()
            editor.putString("accessToken", accessToken)
            editor.putString("refreshToken", refreshToken)
            editor.apply()
        }catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}