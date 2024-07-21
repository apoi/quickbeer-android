package quickbeer.android.ui.adapter.feed

import coil.load
import coil.request.Disposable
import coil.transform.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.databinding.FeedListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.base.ScopeListViewHolder
import quickbeer.android.ui.transformations.ContainerLabelExtractor
import timber.log.Timber

class FeedViewHolder(
    private val binding: FeedListItemBinding
) : ScopeListViewHolder<FeedListModel>(binding.root) {

    private var disposable: Disposable? = null

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
        clear()

        Timber.d("BIND $item")

        when (item.feedItem) {
            is FeedItem.BeerRated -> bindBeerRating(item.feedItem, item, scope)
        }
    }

    private fun bindBeerRating(
        item: FeedItem.BeerRated,
        listModel: FeedListModel,
        scope: CoroutineScope
    ) {
        scope.launch {
            listModel.getBeer(item.beerId)
                .collect { beerState ->
                    val beer = beerState.valueOrNull()

                    if (beer != null) {
                        withContext(Dispatchers.Main) {
                            setBeer(beer)
                        }
                    }
                }
        }
    }

    private fun setBeer(beer: Beer) {
        disposable = binding.background.load(beer.imageUri()) {
            crossfade(itemView.resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(
                ContainerLabelExtractor(LABEL_WIDTH, LABEL_HEIGHT),
                BlurTransformation(itemView.context, LABEL_BLUR)
            )
        }
    }

    override fun unbind() {
        super.unbind()
        disposable?.dispose()
        disposable = null
    }

    private fun clear() {
        binding.background.setImageResource(0)
    }

    companion object {
        private const val LABEL_WIDTH = 500
        private const val LABEL_HEIGHT = 500
        private const val LABEL_BLUR = 15f
    }
}
