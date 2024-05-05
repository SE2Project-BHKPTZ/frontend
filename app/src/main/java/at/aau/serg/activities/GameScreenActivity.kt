package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import at.aau.serg.R
import at.aau.serg.fragments.TrickPredictionFragment
import at.aau.serg.fragments.TrickPredictionViewModel
import at.aau.serg.fragments.GameScreenFivePlayersFragment
import at.aau.serg.fragments.GameScreenFourPlayersFragment
import at.aau.serg.fragments.GameScreenSixPlayersFragment
import at.aau.serg.fragments.GameScreenThreePlayersFragment
import at.aau.serg.models.CardItem

class GameScreenActivity : AppCompatActivity() {
    private val trickViewModel: TrickPredictionViewModel by viewModels()

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

    fun onCardClicked(cardItem: CardItem) {
        val player1CardImageView = findViewById<ImageView>(R.id.ivPlayer1Card)
        val cardResourceId = resources.getIdentifier(
            "card_${cardItem.suit.toString().lowercase()}_${cardItem.value}", "drawable", packageName)
        player1CardImageView.setImageResource(cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)
    }
}