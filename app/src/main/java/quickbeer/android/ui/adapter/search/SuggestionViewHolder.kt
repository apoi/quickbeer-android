package quickbeer.android.ui.adapter.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import quickbeer.android.R

class SuggestionViewHolder(view: View) {

    private val icon: ImageView = view.findViewById(R.id.suggestion_icon)
    private val text: TextView = view.findViewById(R.id.suggestion_text)

    fun bind(result: SearchResult) {
        val res = when (result.type) {
            SearchResult.Type.BEER -> R.drawable.ic_history
            SearchResult.Type.BREWERY -> R.drawable.ic_history
            SearchResult.Type.SEARCH -> R.drawable.ic_history
        }

        text.text = result.text
        icon.setImageResource(res)
    }
}
