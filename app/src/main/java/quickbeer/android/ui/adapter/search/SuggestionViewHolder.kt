package quickbeer.android.ui.adapter.search

import quickbeer.android.R
import quickbeer.android.databinding.SearchViewSuggestionBinding
import quickbeer.android.ui.adapter.simple.ListViewHolder

class SuggestionViewHolder(
    private val binding: SearchViewSuggestionBinding
) : ListViewHolder<SearchSuggestion>(binding.root) {

    override fun bind(suggestion: SearchSuggestion) {
        val res = when (suggestion.type) {
            SearchSuggestion.Type.BEER -> R.drawable.ic_history
            SearchSuggestion.Type.BREWERY -> R.drawable.ic_history
            SearchSuggestion.Type.SEARCH -> R.drawable.ic_history
        }

        binding.suggestionText.text = suggestion.text
        binding.suggestionIcon.setImageResource(res)
    }
}
