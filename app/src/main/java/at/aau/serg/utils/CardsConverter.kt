package at.aau.serg.utils

import at.aau.serg.models.CardItem
import at.aau.serg.models.Suit
import org.json.JSONArray
import org.json.JSONObject

object CardsConverter {
    fun convertCards(cardsJsonArray: JSONArray): Array<CardItem> {
        val cardsList = mutableListOf<CardItem>()
        for (i in 0 until cardsJsonArray.length()) {
            val cardJson = cardsJsonArray.getJSONObject(i)
            val card = convertCard(cardJson)
            cardsList.add(card)
        }

        return cardsList.toTypedArray()
    }

    fun convertCard(cardJson: JSONObject): CardItem {
        return CardItem(
            cardJson.getString("value"),
            stringToSuit(cardJson.getString("suit"))
        )
    }

    private fun stringToSuit(suitString: String): Suit {
        return when (suitString.uppercase()) {
            "HEARTS" -> Suit.HEARTS
            "DIAMONDS" -> Suit.DIAMONDS
            "CLUBS" -> Suit.CLUBS
            "SPADES" -> Suit.SPADES
            else -> throw IllegalArgumentException("Unknown suit: $suitString")
        }
    }
}