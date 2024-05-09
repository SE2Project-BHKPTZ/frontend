package at.aau.serg.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import at.aau.serg.R
import at.aau.serg.fragments.GameScreenFivePlayersFragment
import at.aau.serg.fragments.GameScreenFourPlayersFragment
import at.aau.serg.fragments.GameScreenSixPlayersFragment
import at.aau.serg.fragments.GameScreenThreePlayersFragment
import at.aau.serg.fragments.TrickPredictionFragment
import at.aau.serg.fragments.TrickPredictionViewModel
import at.aau.serg.models.CardItem
import at.aau.serg.network.SocketHandler

class GameScreenActivity : AppCompatActivity() {
    private val trickViewModel: TrickPredictionViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val fragment = supportFragmentManager.findFragmentById(R.id.game_fragment_container_view)
        if (fragment is TrickPredictionFragment) {
            trickViewModel.setRound(10)
        }

        val gameScreen = getGameScreen()
        if (gameScreen != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.game_fragment_container_view, gameScreen)
            transaction.commit()
        }

        val initialCards = intent.getSerializableExtra("cards", Array<CardItem>::class.java)
        val initialTrumpCard = intent.getStringExtra("trump")

        // TODO: Pass initial cards to the Cards fragment

        SocketHandler.on("cardPlayed", ::cardPlayed)
        SocketHandler.on("trickPrediction", ::trickPrediction)
    }

    // only for showing the different GameScreens now
    fun getGameScreen(): Fragment? {
        val randomInt = (3..6).random()
        return when (randomInt){
            3 -> GameScreenThreePlayersFragment()
            4 -> GameScreenFourPlayersFragment()
            5 -> GameScreenFivePlayersFragment()
            6 -> GameScreenSixPlayersFragment()
            else -> null
        }
    }

    fun btnMenuClicked(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun btnChangeFragmentClicked(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.game_fragment_container_view)
        val newFragment = when (fragment) {
            is TrickPredictionFragment -> getGameScreen()
            else -> TrickPredictionFragment()
        }

        if (newFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.game_fragment_container_view, newFragment)
                .commit()
        }
    }

    private fun cardPlayed(socketResponse: Array<Any>) {
        Log.d("Socket", "Received cardPlayed event")
    }

    private fun trickPrediction(socketResponse: Array<Any>) {
        Log.d("Socket", "Received trickPrediction event")
    }
}