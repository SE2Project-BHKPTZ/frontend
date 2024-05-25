package at.aau.serg.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.aau.serg.models.Score

class GameScreenViewModel: ViewModel() {
    private val _scores = MutableLiveData<Array<Score>>()
    val scores: LiveData<Array<Score>> = _scores

    private val _position = MutableLiveData<Int>()
    val position: LiveData<Int> = _position

    fun setScores(scores: Array<Score>) {
        _scores.value = scores
    }

    fun setPosition(position: Int) {
        _position.value = position
    }
}