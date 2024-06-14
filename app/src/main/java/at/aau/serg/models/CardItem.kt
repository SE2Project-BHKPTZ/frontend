package at.aau.serg.models

import java.io.Serializable
import com.google.gson.*
import java.lang.reflect.Type

data class CardItem(val value: String, val suit: Suit): Serializable{
    fun isJester(): Boolean {
        return value === "0"
    }

    fun isWizard(): Boolean {
        return value === "14"
    }
}

class CardItemDeserializer : JsonDeserializer<CardItem> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CardItem {
        val jsonObject = json?.asJsonObject
        val value = jsonObject?.get("value")?.asString ?: ""
        val suit = jsonObject?.get("suit")?.asString?.uppercase() ?: ""
        return CardItem(value, Suit.valueOf(suit))
    }
}
