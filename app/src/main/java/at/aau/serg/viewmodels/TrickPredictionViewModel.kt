package at.aau.serg.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrickPredictionViewModel: ViewModel() {
    private val _round = MutableLiveData<Int>()
    val round: LiveData<Int> = _round

    fun setRound(data: Int) {
        _round.value = data
    }
}