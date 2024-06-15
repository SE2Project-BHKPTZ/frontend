package models

import at.aau.serg.models.CardItem
import at.aau.serg.models.Deck
import at.aau.serg.models.GameRecovery
import at.aau.serg.models.PlayedCard
import at.aau.serg.models.Player
import at.aau.serg.models.Round
import at.aau.serg.models.Score
import at.aau.serg.models.SubRound
import at.aau.serg.models.Suit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameRecoveryTest {
    private val player = Player("12345", "name")
    private val predictions = mapOf("12345" to 2)
    private val card = CardItem("10", Suit.CLUBS)
    private val subround = SubRound(listOf(PlayedCard(player, card)), "12345")
    private val deck = Deck(listOf(listOf(card)), card)
    private val round = Round(predictions, listOf(subround), listOf(""), deck)

    @Test
    fun `Game recovery instantiation`() {
        val gameRecovery = GameRecovery(listOf(player), 5, round, emptyMap(), "12345", 2)

        assertEquals(listOf(player), gameRecovery.players)
        assertEquals(5, gameRecovery.maxRounds)
        assertEquals(2, gameRecovery.currentRound)
        assertEquals(round, gameRecovery.round)
        assertEquals(emptyMap<String, Score>(), gameRecovery.playerScore)
        assertEquals("12345", gameRecovery.nextPlayer)
    }
}