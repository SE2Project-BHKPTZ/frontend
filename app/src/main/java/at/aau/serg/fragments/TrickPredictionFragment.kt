package at.aau.serg.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.aau.serg.R
import com.google.android.material.slider.Slider

private const val ARG_ROUND = "round"

class TrickPredictionFragment : Fragment() {
    private var round: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            round = it.getInt(ARG_ROUND)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trick_prediction, container, false)

        val trickPredictionSlider: Slider =  view.findViewById(R.id.trickPredictionSlider)
        val scoreTextView: TextView = view.findViewById(R.id.predictionScoreTV)

        trickPredictionSlider.addOnChangeListener { _, value, _ ->
            val predictionPoints = "${calculateReachablePoints(value.toInt())} Points"
            scoreTextView.text = predictionPoints
        }

        trickPredictionSlider.valueTo = round.toFloat()

        return view
    }

    private fun calculateReachablePoints(prediction: Int): Int {
        return prediction * 20 + 20;
    }

    companion object {
        @JvmStatic
        fun newInstance(round: Int) =
            TrickPredictionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ROUND, round)
                }
            }
    }
}