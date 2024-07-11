package quickbeer.android.ui.adapter.beer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.BeerListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.rating.Rating
import quickbeer.android.ui.adapter.base.ScopeListViewHolder

class BeerListViewHolder(
    private val binding: BeerListItemBinding
) : ScopeListViewHolder<BeerListModel>(binding.root) {

    override fun bind(item: BeerListModel, scope: CoroutineScope) {
        clear()
        scope.launch {
            combine(item.getBeer(), item.getRating(), ::Pair)
                .collectLatest { (beerState, ratingState) ->
                    withContext(Dispatchers.Main) { updateState(beerState, ratingState) }
                }
        }
    }

    private fun updateState(beerState: State<Beer>, ratingState: State<Rating>) {
        val beer = beerState.valueOrNull()
        val rating = ratingState.valueOrNull()

        if (beer != null) {
            setBeer(beer, rating)
        }
    }

    private fun setBeer(beer: Beer, rating: Rating?) {
        // Don't show anything if the name isn't loaded yet.
        // This prevents the rating label to be shown with empty details.
        if (beer.name.isNullOrEmpty()) return

        if (beer.isTicked() || rating != null) {
            binding.beerScore.setTextColor(itemView.context.getColor(R.color.text_dark))
            binding.beerScore.setBackgroundResource(R.drawable.score_rated)
        } else {
            binding.beerScore.setTextColor(itemView.context.getColor(R.color.text_light))
            binding.beerScore.setBackgroundResource(R.drawable.score_unrated)
        }

        binding.beerScore.text = beer.rating().takeIf { it >= 0 }?.toString() ?: "?"
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
