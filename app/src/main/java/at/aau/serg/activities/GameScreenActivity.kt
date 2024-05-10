package at.aau.serg.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import at.aau.serg.network.SocketHandler
import org.json.JSONObject

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

        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewGame)
        if (fragment is TrickPredictionFragment) {
            trickViewModel.setRound(10)
        }

        val gameScreen = getGameScreen()
        if (gameScreen != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainerViewGame, gameScreen)
            transaction.commit()
        }

        SocketHandler.on("game:updateUserScore", ::updateUserScore)
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
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewGame)
        val newFragment = when (fragment) {
            is TrickPredictionFragment -> getGameScreen()
            else -> TrickPredictionFragment()
        }

        if (newFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerViewGame, newFragment)
                .commit()
        }
    }

    private fun updateUserScore(socketResponse: Array<Any>) {
        Log.d("Socket", "Received updateUserScore event")

        val playerData = (socketResponse[0] as JSONObject)
        val playerUUID = playerData.getString("playerUUID")
        val playerPoints = playerData.getInt("points")
        Log.d("Game", "$playerUUID has $playerPoints points")

        val tvPlayer1Points = findViewById<TextView>(R.id.tvPlayer1Points)
        tvPlayer1Points?.text = playerPoints.toString()

    }
}