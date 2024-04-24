package at.aau.serg.fragments

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.aau.serg.R
import at.aau.serg.placeholder.CardContent

class CardsFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = CardsRecyclerViewAdapter(CardContent.ITEMS)
                val overlapWidth = resources.getDimensionPixelSize(R.dimen.overlap_width)
                addItemDecoration(OverlapDecoration(overlapWidth))
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CardsFragment().apply {
                arguments = Bundle().apply {
                }
            }
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