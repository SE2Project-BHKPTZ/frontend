package at.aau.serg.androidutils

import androidx.annotation.DrawableRes
import at.aau.serg.R

object CardUtils {
    val cardResources = mapOf(
        "card_clubs_0" to R.drawable.card_clubs_0,
        "card_clubs_1" to R.drawable.card_clubs_1,
        "card_clubs_2" to R.drawable.card_clubs_2,
        "card_clubs_3" to R.drawable.card_clubs_3,
        "card_clubs_4" to R.drawable.card_clubs_4,
        "card_clubs_5" to R.drawable.card_clubs_5,
        "card_clubs_6" to R.drawable.card_clubs_6,
        "card_clubs_7" to R.drawable.card_clubs_7,
        "card_clubs_8" to R.drawable.card_clubs_8,
        "card_clubs_9" to R.drawable.card_clubs_9,
        "card_clubs_10" to R.drawable.card_clubs_10,
        "card_clubs_11" to R.drawable.card_clubs_11,
        "card_clubs_12" to R.drawable.card_clubs_12,
        "card_clubs_13" to R.drawable.card_clubs_13,
        "card_clubs_14" to R.drawable.card_clubs_14,

        "card_diamonds_0" to R.drawable.card_diamonds_0,
        "card_diamonds_1" to R.drawable.card_diamonds_1,
        "card_diamonds_2" to R.drawable.card_diamonds_2,
        "card_diamonds_3" to R.drawable.card_diamonds_3,
        "card_diamonds_4" to R.drawable.card_diamonds_4,
        "card_diamonds_5" to R.drawable.card_diamonds_5,
        "card_diamonds_6" to R.drawable.card_diamonds_6,
        "card_diamonds_7" to R.drawable.card_diamonds_7,
        "card_diamonds_8" to R.drawable.card_diamonds_8,
        "card_diamonds_9" to R.drawable.card_diamonds_9,
        "card_diamonds_10" to R.drawable.card_diamonds_10,
        "card_diamonds_11" to R.drawable.card_diamonds_11,
        "card_diamonds_12" to R.drawable.card_diamonds_12,
        "card_diamonds_13" to R.drawable.card_diamonds_13,
        "card_diamonds_14" to R.drawable.card_diamonds_14,

        "card_hearts_0" to R.drawable.card_hearts_0,
        "card_hearts_1" to R.drawable.card_hearts_1,
        "card_hearts_2" to R.drawable.card_hearts_2,
        "card_hearts_3" to R.drawable.card_hearts_3,
        "card_hearts_4" to R.drawable.card_hearts_4,
        "card_hearts_5" to R.drawable.card_hearts_5,
        "card_hearts_6" to R.drawable.card_hearts_6,
        "card_hearts_7" to R.drawable.card_hearts_7,
        "card_hearts_8" to R.drawable.card_hearts_8,
        "card_hearts_9" to R.drawable.card_hearts_9,
        "card_hearts_10" to R.drawable.card_hearts_10,
        "card_hearts_11" to R.drawable.card_hearts_11,
        "card_hearts_12" to R.drawable.card_hearts_12,
        "card_hearts_13" to R.drawable.card_hearts_13,
        "card_hearts_14" to R.drawable.card_hearts_14,

        "card_spades_0" to R.drawable.card_spades_0,
        "card_spades_1" to R.drawable.card_spades_1,
        "card_spades_2" to R.drawable.card_spades_2,
        "card_spades_3" to R.drawable.card_spades_3,
        "card_spades_4" to R.drawable.card_spades_4,
        "card_spades_5" to R.drawable.card_spades_5,
        "card_spades_6" to R.drawable.card_spades_6,
        "card_spades_7" to R.drawable.card_spades_7,
        "card_spades_8" to R.drawable.card_spades_8,
        "card_spades_9" to R.drawable.card_spades_9,
        "card_spades_10" to R.drawable.card_spades_10,
        "card_spades_11" to R.drawable.card_spades_11,
        "card_spades_12" to R.drawable.card_spades_12,
        "card_spades_13" to R.drawable.card_spades_13,
        "card_spades_14" to R.drawable.card_spades_14
    )

    @DrawableRes
    fun getResourceId(cardKey: String): Int {
        return cardResources[cardKey] .takeIf { it != 0 } ?: R.drawable.card_diamonds_1
    }
}