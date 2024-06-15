package at.aau.serg.models

import java.io.Serializable

data class Deck (val hands: List<List<CardItem>>, val trump: CardItem): Serializable