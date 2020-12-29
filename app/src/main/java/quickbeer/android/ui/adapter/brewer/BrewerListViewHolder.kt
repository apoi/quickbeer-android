package quickbeer.android.ui.adapter.brewer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BrewerListItemBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.ui.adapter.simple.ScopeListViewHolder

class BrewerListViewHolder(
    private val binding: BrewerListItemBinding
) : ScopeListViewHolder<BrewerListModel>(binding.root) {

    override fun bind(item: BrewerListModel, scope: CoroutineScope) {
        scope.launch {
            item.getBrewer().collect {
                withContext(Dispatchers.Main) { updateState(it) }
            }
        }
    }

    private fun updateState(state: State<Brewer>) {
        when (state) {
            is State.Success -> setBrewer(state.value)
        }
    }

    private fun setBrewer(brewer: Brewer) {
        // Don't show anything if the name isn't loaded yet.
        // This prevents the rating label to be shown with empty details.
        if (brewer.name.isNullOrEmpty()) return

        binding.brewerName.text = brewer.name
        binding.brewerLocation.text = brewer.city
    }

    override fun unbind() {
        super.unbind()

        binding.brewerName.text = ""
        binding.brewerLocation.text = ""
    }
}
