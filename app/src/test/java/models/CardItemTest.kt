package models

import at.aau.serg.models.CardItem
import at.aau.serg.models.Suit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CardItemTest {
    @Test
    fun `CardItem instantiation`() {
        val card = CardItem("Ace", Suit.HEARTS)
        assertEquals("Ace", card.value)
        assertEquals(Suit.HEARTS, card.suit)
    }

    @Test
    fun `CardItem isWizard`() {
        val card = CardItem("14", Suit.DIAMONDS)
        assertTrue(card.isWizard())
    }

    @Test
    fun `CardItem isNotWizard`() {
        val card = CardItem("Ace", Suit.SPADES)
        assertFalse(card.isWizard())
    }

    @Test
    fun `CardItem isJester`() {
        val card = CardItem("0", Suit.CLUBS)
        assertTrue(card.isJester())
    }

    @Test
    fun `CardItem isNotJester`() {
        val card = CardItem("Ace", Suit.HEARTS)
        assertFalse(card.isJester())
    }
}