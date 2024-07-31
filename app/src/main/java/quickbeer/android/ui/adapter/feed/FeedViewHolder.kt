package quickbeer.android.ui.adapter.feed

import coil.load
import coil.request.Disposable
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.Constants
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
        setUser(item.userName)

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

    private fun setUser(userName: String) {
        binding.line1.text = "$userName rated"

        binding.avatar.load(Constants.USER_AVATAR_PATH.format(userName)) {
            crossfade(itemView.resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(CircleCropTransformation())
        }
    }

    private fun setBeer(beer: Beer) {
        val details = listOfNotNull(
            beer.styleName,
            beer.alcohol
                ?.takeIf { it > 0 }
                ?.toString()
                ?.let { "ABV %s%%".format(it) }
        )

        binding.line2.text = beer.name
        binding.line3.text = details.joinToString(", ")

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
        binding.line1.text = ""
        binding.line2.text = ""
        binding.line3.text = ""

        binding.avatar.setImageResource(0)
        binding.background.setImageResource(0)
    }

    companion object {
        private const val LABEL_WIDTH = 500
        private const val LABEL_HEIGHT = 500
        private const val LABEL_BLUR = 15f
    }
}
