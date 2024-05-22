package at.aau.serg.androidutils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import at.aau.serg.models.Score
import at.aau.serg.utils.GameUtils.calculatePositionOfPlayer
import at.aau.serg.viewmodels.GameScreenViewModel

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
}