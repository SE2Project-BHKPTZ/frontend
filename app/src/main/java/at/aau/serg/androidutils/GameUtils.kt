package at.aau.serg.androidutils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import at.aau.serg.fragments.GameScreenFivePlayersFragment
import at.aau.serg.fragments.GameScreenFourPlayersFragment
import at.aau.serg.fragments.GameScreenSixPlayersFragment
import at.aau.serg.fragments.GameScreenThreePlayersFragment
import at.aau.serg.models.CardItem
import at.aau.serg.models.CardItemDeserializer
import at.aau.serg.models.GameRecovery
import at.aau.serg.models.Lobby
import at.aau.serg.models.Score
import at.aau.serg.models.ScoreDeserializer
import at.aau.serg.utils.GameUtils.calculatePositionOfPlayer
import at.aau.serg.viewmodels.GameScreenViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.Serializable

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

    inline fun <reified T> convertSerializableToArray(serializable: Any?): Array<T>? {
        return when (serializable) {
            is Array<*> -> {
                if (serializable.all { it is T }) {
                    serializable.filterIsInstance<T>().toTypedArray()
                } else {
                    null
                }
            }
            else -> null
        }
    }

    fun parseLobbyJson(jsonObject: JSONObject): Lobby {
        val gson = Gson()
        return gson.fromJson(jsonObject.toString(), Lobby::class.java)
    }

    fun parseGameDataJson(jsonObject: JSONObject): GameRecovery {
        val gson = GsonBuilder()
            .registerTypeAdapter(CardItem::class.java, CardItemDeserializer())
            .registerTypeAdapter(Score::class.java , ScoreDeserializer())
            .create()

        return gson.fromJson(jsonObject.toString(), GameRecovery::class.java)
    }

    inline fun <reified T : java.io.Serializable> Bundle.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
}