package quickbeer.android.ui.adapter.search

import quickbeer.android.R
import quickbeer.android.databinding.SearchViewSuggestionBinding
import quickbeer.android.ui.adapter.simple.ListViewHolder

class SuggestionViewHolder(
    private val binding: SearchViewSuggestionBinding
) : ListViewHolder<SearchResult>(binding.root) {

    override fun bind(result: SearchResult) {
        val res = when (result.type) {
            SearchResult.Type.BEER -> R.drawable.ic_history
            SearchResult.Type.BREWERY -> R.drawable.ic_history
            SearchResult.Type.SEARCH -> R.drawable.ic_history
        }

        binding.suggestionText.text = result.text
        binding.suggestionIcon.setImageResource(res)
    }
}
