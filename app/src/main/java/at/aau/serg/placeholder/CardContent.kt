package at.aau.serg.placeholder

import at.aau.serg.models.CardItem
import at.aau.serg.models.Suit
import java.util.ArrayList
import java.util.HashMap

object CardContent {

    val ITEMS: MutableList<CardItem> = ArrayList()
    val ITEM_MAP: MutableMap<String, CardItem> = HashMap()

    // TODO: Get cards using HTTP call or as parameters
    private val COUNT = 10

    init {
        for (i in 2..COUNT) {
            addItem(createPlaceholderItem(i))
        }
    }

    private fun addItem(item: CardItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.value, item)
    }

    private fun createPlaceholderItem(position: Int): CardItem {
        return CardItem(position.toString(), Suit.CLUBS)
    }
}