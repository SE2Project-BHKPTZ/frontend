package at.aau.serg.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import at.aau.serg.R
import at.aau.serg.adapters.CardsRecyclerViewAdapter
import at.aau.serg.models.CardItem
import at.aau.serg.placeholder.CardContent
import at.aau.serg.viewmodels.CardsViewModel
import at.aau.serg.viewmodels.TrickPredictionViewModel

class CardsFragment : Fragment() {
    private val viewModel: CardsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = CardsRecyclerViewAdapter(viewModel, viewLifecycleOwner) {
                    cardItem ->  onCardClicked(cardItem)
                }
                val overlapWidth = resources.getDimensionPixelSize(R.dimen.overlapWidth)
                addItemDecoration(OverlapDecoration(overlapWidth))
            }
        }

        return view
    }

    private fun onCardClicked(cardItem: CardItem) {
        Toast.makeText(context, "Card clicked: ${cardItem.value} of ${cardItem.suit}", Toast.LENGTH_SHORT).show()
    }
}


class OverlapDecoration(private val overlapWidth: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == 0) {
            outRect.left = 0
        } else {
            outRect.left = -overlapWidth
        }
    }
}