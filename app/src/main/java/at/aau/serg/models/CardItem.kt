package at.aau.serg.models

import java.io.Serializable

data class CardItem(val value: String, val suit: Suit): Serializable{

    fun isJester(): Boolean {
        return value === "0"
    }

    fun isWizard(): Boolean {
        return value === "14"
    }
}