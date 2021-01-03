package quickbeer.android.ui.adapter.beer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.ui.adapter.simple.ScopeListViewHolder
import quickbeer.android.ui.view.Score

class BeerListViewHolder(
    private val binding: BeerListItemBinding
) : ScopeListViewHolder<BeerListModel>(binding.root) {

    override fun bind(item: BeerListModel, scope: CoroutineScope) {
        clear()
        scope.launch {
            item.getBeer().collect {
                withContext(Dispatchers.Main) { updateState(it) }
            }
        }
    }

    private fun updateState(state: State<Beer>) {
        when (state) {
            is State.Loading -> state.value?.let(::setBeer)
            is State.Success -> setBeer(state.value)
        }
    }

    private fun setBeer(beer: Beer) {
        // Don't show anything if the name isn't loaded yet.
        // This prevents the rating label to be shown with empty details.
        if (beer.name.isNullOrEmpty()) return

        if (beer.isTicked()) {
            binding.beerScore.text = ""
            binding.beerScore.setBackgroundResource(Score.fromTick(beer.tickValue).resource)
        } else {
            binding.beerScore.text = Score.fromRating(beer.rating())
            binding.beerScore.setBackgroundResource(R.drawable.score_unrated)
        }

        binding.beerName.text = beer.name
        binding.beerStyle.text = beer.styleName
        binding.brewerName.text = beer.brewerName
    }

    private fun clear() {
        binding.beerName.text = ""
        binding.beerStyle.text = ""
        binding.brewerName.text = ""
        binding.beerScore.setBackgroundResource(R.drawable.score_unrated)
    }
}
