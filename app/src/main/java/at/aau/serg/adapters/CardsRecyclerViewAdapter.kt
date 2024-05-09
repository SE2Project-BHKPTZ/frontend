package at.aau.serg.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.R
import at.aau.serg.databinding.FragmentCardBinding
import at.aau.serg.models.CardItem
import at.aau.serg.viewmodels.CardsViewModel

class CardsRecyclerViewAdapter(
    private val viewModel: CardsViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onCardClick: (CardItem) -> Unit
) : RecyclerView.Adapter<CardsRecyclerViewAdapter.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var cards: Array<CardItem> = arrayOf()

    init {
        viewModel.cards.observe(lifecycleOwner, Observer { data ->
            cards = data
            notifyDataSetChanged()
        })
    }

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
        val card = cards[position]
        setCardImage(holder, card)
        setCardBackground(holder, position)
        setupClickListener(holder, card, position)
    }

    private fun setCardImage(holder: ViewHolder, card: CardItem) {
        val cardResourceId = holder.itemView.context.resources.getIdentifier(
            "card_${card.suit.toString().lowercase()}_${card.value}", "drawable", holder.itemView.context.packageName)
        holder.imageView.setImageResource(cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)
    }

    private fun setCardBackground(holder: ViewHolder, position: Int) {
        holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context,
            if (position == selectedPosition) R.drawable.card_border_selected else R.drawable.card_border_default)
    }

    private fun setupClickListener(holder: ViewHolder, card: CardItem, position: Int) {
        holder.itemView.setOnClickListener {
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION
            } else {
                val previousItem = selectedPosition
                selectedPosition = position

                if (previousItem != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousItem)
                }
            }
            notifyItemChanged(position)

            onCardClick(card)
        }
    }

    override fun getItemCount(): Int = cards.size

    inner class ViewHolder(binding: FragmentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.cardImageView

        override fun toString(): String {
            return super.toString()
        }
    }

}