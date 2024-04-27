package at.aau.serg.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.aau.serg.R

class GameScreenFivePlayersFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_gamescreen_five_players, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            GameScreenFivePlayersFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}