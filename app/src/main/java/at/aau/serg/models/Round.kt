package at.aau.serg.models

import java.io.Serializable

data class Round (val predictions: List<String>, val subrounds: List<SubRound>, val scores: List<Any>, val deck: Deck): Serializable