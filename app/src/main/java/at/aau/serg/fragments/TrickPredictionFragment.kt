package at.aau.serg.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import at.aau.serg.R
import at.aau.serg.activities.GameScreenActivity
import at.aau.serg.network.SocketHandler
import at.aau.serg.viewmodels.TrickPredictionViewModel
import com.google.android.material.slider.Slider


class TrickPredictionFragment : Fragment() {
    private val viewModel: TrickPredictionViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trick_prediction, container, false)

        val trickPredictionSlider: Slider =  view.findViewById(R.id.sliderTrickPrediction)
        val scoreTextView: TextView = view.findViewById(R.id.tvPredictionScore)
        val confirmPredictionButton: Button = view.findViewById(R.id.btnConfirmPrediction)

        trickPredictionSlider.addOnChangeListener { _, value, _ ->
            val predictionPoints = "${calculateReachablePoints(value.toInt())} Points"
            scoreTextView.text = predictionPoints
        }

        confirmPredictionButton.setOnClickListener {
            Log.d("Prediction", "Sending prediction")
            (activity as GameScreenActivity).setPlayerGameScreen()
            SocketHandler.emit("trickPrediction", trickPredictionSlider.value.toInt())
        }

        viewModel.round.observe(viewLifecycleOwner) {round ->
            setMaximumPrediction(round)
        }

        return view
    }

    private fun setMaximumPrediction(round: Int) {
        view?.findViewById<Slider>(R.id.sliderTrickPrediction)?.valueTo = round.toFloat()
    }

    private fun calculateReachablePoints(prediction: Int): Int {
        return prediction * 10 + 20;
    }
}