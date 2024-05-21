package at.aau.serg.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import at.aau.serg.R
import at.aau.serg.models.Score
import at.aau.serg.viewmodels.GameScreenViewModel
import kotlin.properties.Delegates

class GameScreenThreePlayersFragment : Fragment() {
    private val viewModel: GameScreenViewModel by activityViewModels()

    private var position by Delegates.notNull<Int>()
    private val playerCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gamescreen_three_players, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.position.observe(viewLifecycleOwner) {
            position = it
        }

        viewModel.scores.observe(viewLifecycleOwner) { scores ->
            scores.forEach { score: Score ->
                val scorePosition = calculatePositionOfPlayer(score.position, position)
                val packageName = requireContext().packageName
                val scoreTextView = view.findViewById<TextView>(resources.getIdentifier("tvPlayer${scorePosition}Points", "id", packageName))
                if (scoreTextView != null) {
                    scoreTextView.text = score.score
                }
            }
        }
    }

    private fun calculatePositionOfPlayer(serverIdx: Int, localIdx: Int): Int{
        return when(localIdx - serverIdx){
            0 -> 1
            1 -> playerCount
            2 -> playerCount - 1
            3 -> playerCount - 2
            4 -> playerCount - 3
            5 -> playerCount - 4
            else -> 1 + ((localIdx - serverIdx) * -1)
        }
    }
}