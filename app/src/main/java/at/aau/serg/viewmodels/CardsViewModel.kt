package at.aau.serg.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.aau.serg.models.CardItem

class CardsViewModel : ViewModel() {
    private val _cards = MutableLiveData<Array<CardItem>>()
    val cards: LiveData<Array<CardItem>> = _cards

    fun setCards(data: Array<CardItem>) {
        _cards.value = data
    }
}