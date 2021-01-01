package quickbeer.android.ui.adapter.suggestion

import quickbeer.android.R
import quickbeer.android.databinding.SearchViewSuggestionBinding
import quickbeer.android.ui.adapter.simple.ListViewHolder

class SuggestionViewHolder(
    private val binding: SearchViewSuggestionBinding
) : ListViewHolder<SuggestionListModel>(binding.root) {

    override fun bind(suggestion: SuggestionListModel) {
        val res = when (suggestion.type) {
            SuggestionListModel.Type.SEARCH -> R.drawable.ic_history
            SuggestionListModel.Type.BEER -> R.drawable.ic_action_web
            SuggestionListModel.Type.BREWER -> R.drawable.ic_search
        }

        binding.suggestionText.text = suggestion.text
        binding.suggestionIcon.setImageResource(res)
    }
}
