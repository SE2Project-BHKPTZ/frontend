package at.aau.serg.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import at.aau.serg.R
import at.aau.serg.androidutils.GameUtils.updateScores
import at.aau.serg.viewmodels.GameScreenViewModel
import kotlin.properties.Delegates

class GameScreenFourPlayersFragment : Fragment() {

    private val viewModel: GameScreenViewModel by activityViewModels()
    private var position by Delegates.notNull<Int>()
    private val playerCount = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gamescreen_four_players, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.position.observe(viewLifecycleOwner) { newPosition ->
            position = newPosition
            updateScores(view, requireContext(), viewModel, viewLifecycleOwner, { position }, playerCount)
        }
    }
}