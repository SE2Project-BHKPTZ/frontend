package models

import at.aau.serg.models.CardItem
import at.aau.serg.models.Suit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CardItemTest {
    @Test
    fun `CardItem instantiation`() {
        val card = CardItem("Ace", Suit.HEARTS)
        assertEquals("Ace", card.value)
        assertEquals(Suit.HEARTS, card.suit)
    }
}