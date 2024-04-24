package at.aau.serg.fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.databinding.FragmentCardBinding
import at.aau.serg.placeholder.CardContent.CardItem


/**
 * [RecyclerView.Adapter] that can display a [CardItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CardsRecyclerViewAdapter(
    private val values: List<CardItem>
) : RecyclerView.Adapter<CardsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val cardResourceId = holder.itemView.context.resources.getIdentifier(
            "cardclubs${item.id}", "drawable", holder.itemView.context.packageName)
        if (cardResourceId != 0) {
            holder.imageView.setImageResource(cardResourceId)
        } else {
            holder.imageView.setImageResource(R.drawable.cardclubs2)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.cardImageView

        override fun toString(): String {
            return super.toString()
        }
    }

}