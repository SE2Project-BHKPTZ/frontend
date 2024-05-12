package at.aau.serg.models

import org.json.JSONObject
import java.io.Serializable

data class CardItem(val value: String, val suit: Suit): Serializable{
}