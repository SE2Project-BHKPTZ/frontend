package at.aau.serg.models

import java.io.Serializable

data class SubRound (val cardsPlayed: List<PlayedCard>, val stichPlayer: String): Serializable

data class PlayedCard(
    val player: Player,
    val card: CardItem
) : Serializable