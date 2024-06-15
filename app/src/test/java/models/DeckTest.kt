package models

import at.aau.serg.models.CardItem
import at.aau.serg.models.Deck
import at.aau.serg.models.Suit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeckTest {
    @Test
    fun `deck instantiation`() {
        val card = CardItem("10", Suit.CLUBS)
        val deck = Deck(listOf(listOf(card)), card)
        assertEquals(card, deck.trump)
        assertEquals(listOf(listOf(card)), deck.hands)
    }
}