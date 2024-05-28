package at.aau.serg.androidutils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import at.aau.serg.fragments.GameScreenFivePlayersFragment
import at.aau.serg.fragments.GameScreenFourPlayersFragment
import at.aau.serg.fragments.GameScreenSixPlayersFragment
import at.aau.serg.fragments.GameScreenThreePlayersFragment
import at.aau.serg.models.CardItem
import at.aau.serg.models.Score
import at.aau.serg.utils.GameUtils.calculatePositionOfPlayer
import at.aau.serg.viewmodels.GameScreenViewModel
import org.json.JSONObject

object GameUtils {
    fun updateScores(
        view: View,
        context: Context,
        viewModel: GameScreenViewModel,
        lifecycleOwner: LifecycleOwner,
        positionProvider: () -> Int,
        playerCount: Int
    ) {
        viewModel.scores.observe(lifecycleOwner) { scores ->
            val position = positionProvider()
            scores.forEach { score: Score ->
                val scorePosition = calculatePositionOfPlayer(score.position, position, playerCount)
                val packageName = context.packageName
                val scoreTextView = view.findViewById<TextView>(context.resources.getIdentifier("tvPlayer${scorePosition}Points", "id", packageName))
                scoreTextView?.text = score.score
            }
        }
    }

    fun getPlayerGameScreen(playerCount: Int): Fragment? {
        return when (playerCount){
            3 -> GameScreenThreePlayersFragment()
            4 -> GameScreenFourPlayersFragment()
            5 -> GameScreenFivePlayersFragment()
            6 -> GameScreenSixPlayersFragment()
            else -> null
        }
    }

    fun cardItemToJson(cardItem: CardItem): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("value", cardItem.value)
        jsonObject.put("suit", cardItem.suit.toString())
        return jsonObject
    }

    fun getMaxRoundCount(playerCount: Int): Int {
        return when(playerCount) {
            3 -> 20
            4 -> 15
            5 -> 12
            6 -> 10
            else -> 20
        }
    }
}