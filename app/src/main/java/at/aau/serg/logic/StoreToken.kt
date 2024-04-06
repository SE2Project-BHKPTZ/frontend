package at.aau.serg.logic

import android.content.Context
import android.content.ContextWrapper
import org.json.JSONException

class StoreToken {
    fun storeTokens(accessToken: String, refreshToken: String, context: ContextWrapper){
        try{

            val sharedPreferences = context.getSharedPreferences("Wizard_Token", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("accessToken", accessToken)
            editor.putString("refreshToken", refreshToken)
            editor.apply()
        }catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}