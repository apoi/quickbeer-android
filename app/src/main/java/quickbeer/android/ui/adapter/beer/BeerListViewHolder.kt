package quickbeer.android.ui.adapter.beer

import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    val beer = beerState.valueOrNull()
                    val rating = ratingState.valueOrNull()

                    if (beer != null) {
                        withContext(Dispatchers.Main) {
                            setBeer(beer, rating)
                        }
                    }
                }
        }
    }

    private fun setBeer(beer: Beer, rating: Rating?) {
        // Don't show anything if the name isn't loaded yet.
        // This prevents the rating label to be shown with empty details.
        if (beer.name.isNullOrEmpty()) return

        val score = beer.rating().takeIf { it >= 0 }?.toString() ?: "?"

        if (beer.isTicked() || rating != null) {
            binding.beerScoreRated.text = score
            binding.beerScoreRated.isVisible = true
            binding.beerScoreUnrated.isVisible = false
        } else {
            binding.beerScoreUnrated.text = score
            binding.beerScoreUnrated.isVisible = true
            binding.beerScoreRated.isVisible = false
        }

        binding.beerName.text = beer.name
        binding.beerStyle.text = beer.styleName
        binding.brewerName.text = beer.brewerName
    }

    private fun clear() {
        binding.beerName.text = ""
        binding.beerStyle.text = ""
        binding.brewerName.text = ""
        binding.beerScoreRated.text = ""
        binding.beerScoreUnrated.text = ""
        binding.beerScoreRated.isVisible = false
        binding.beerScoreUnrated.isVisible = true
    }
}
