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

class FeedViewHolder(
    private val binding: FeedListItemBinding
) : ScopeListViewHolder<FeedListModel>(binding.root) {

    private var disposable: Disposable? = null

    override fun bind(item: FeedListModel, scope: CoroutineScope) {
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
            listModel.getBeer(item.beerId).collect {
                it.valueOrNull()?.let { beer ->
                    withContext(Dispatchers.Main) {
                        setBeer(beer)
                    }
                }
            }
        }
    }

    private fun setBeer(beer: Beer) {
        binding.name.text = beer.name

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
        clear()
    }

    private fun clear() {
        binding.name.text = ""
        binding.background.setImageResource(0)
    }

    companion object {
        private const val LABEL_WIDTH = 500
        private const val LABEL_HEIGHT = 500
        private const val LABEL_BLUR = 15f
    }
}
