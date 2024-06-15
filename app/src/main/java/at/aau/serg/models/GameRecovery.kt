package at.aau.serg.models

import java.io.Serializable

data class GameRecovery (val players: List<Player>, val maxRounds: Int, val round: Round, val playerScore: Map<String, Score>, val nextPlayer: String, val currentRound: Int): Serializable