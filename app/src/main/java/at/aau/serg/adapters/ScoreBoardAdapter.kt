package at.aau.serg.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.models.LobbyPlayer
import at.aau.serg.models.Score

class ScoreBoardAdapter (private val scores: Map<String, Score>, private val players: Array<LobbyPlayer>) :
    RecyclerView.Adapter<ScoreBoardAdapter.ViewHolder>() {

    private val sortedScoresList = scores.entries.sortedByDescending { it.value.score.toInt() }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeTextView: TextView = view.findViewById(R.id.placeTextView)
        val playerNameTextView: TextView = view.findViewById(R.id.playerNameTextView)
        val scoreTextView: TextView = view.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.score_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d("Adapter", position.toString())
        val (key, score) = sortedScoresList[position]
        val positionVal = (position+1).toString()
        viewHolder.placeTextView.text = positionVal
        viewHolder.playerNameTextView.text = findPlayerName(key)?.name ?: ""
        viewHolder.scoreTextView.text = score.score
    }

    override fun getItemCount() = scores.size

    private fun findPlayerName(uuid: String): LobbyPlayer? {
        return players.find { it.uuid == uuid }
    }
}