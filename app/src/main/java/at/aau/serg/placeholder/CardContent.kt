package at.aau.serg.placeholder

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
        ITEM_MAP.put(item.id, item)
    }

    private fun createPlaceholderItem(position: Int): CardItem {
        return CardItem(position.toString(), "Item " + position, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    // TODO: Change parameters to id (card value, color)
    data class CardItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}