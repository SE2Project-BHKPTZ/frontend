package at.aau.serg.models

import android.util.Log
import java.io.Serializable

data class CardItem(val value: String, val suit: Suit): Serializable{

    fun isJester(): Boolean {
        return value === "0"
    }

    fun isWizard(): Boolean {
        Log.i("tempLog", "card has value: $value")
        return value === "14"
    }
}