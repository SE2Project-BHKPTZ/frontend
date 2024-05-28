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
    private val onCardClick: (CardItem) -> Unit,
    private var firstPlayedCard: CardItem?,
) : RecyclerView.Adapter<CardsRecyclerViewAdapter.ViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    private var cards: Array<CardItem> = arrayOf()
    private var playableCards: Set<CardItem> = emptySet()

    init {
        viewModel.cards.observe(lifecycleOwner, Observer { data ->
            cards = data
            updatePlayableCards()
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
        setCardBackground(holder, card)
        setupClickListener(holder, card, position)
    }

    private fun setCardImage(holder: ViewHolder, card: CardItem) {
        val cardResourceId = holder.itemView.context.resources.getIdentifier(
            "card_${card.suit.toString().lowercase()}_${card.value}", "drawable", holder.itemView.context.packageName)
        holder.imageView.setImageResource(cardResourceId.takeIf { it != 0 } ?: R.drawable.card_diamonds_1)
    }

    private fun setCardBackground(holder: ViewHolder, card: CardItem) {
        val backgroundResource = if (playableCards.contains(card)){
            R.drawable.card_border_playable
        } else{
            R.drawable.card_border_default
        }
        holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, backgroundResource)
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

    fun removeCard(cardItem: CardItem) {
        viewModel.removeCard(cardItem)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = cards.size

    fun updateFirstPlayedCard(card: CardItem?) {
        firstPlayedCard = card
        updatePlayableCards()
        notifyDataSetChanged()
    }

    private fun updatePlayableCards() {
        playableCards = if (firstPlayedCard == null || firstPlayedCard!!.isJester() || firstPlayedCard!!.isWizard()) {
            cards.toSet()
        } else {
            val requiredSuit = firstPlayedCard!!.suit
            val hasRequiredSuit = cards.any { it.suit == requiredSuit }
            if (hasRequiredSuit) {
                cards.filter { (it.suit == requiredSuit) || it.isWizard() || it.isJester() }.toSet()
            } else {
                cards.toSet()
            }
        }
    }

    inner class ViewHolder(binding: FragmentCardBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: ImageView = binding.ivCard

        override fun toString(): String {
            return super.toString()
        }
    }

}