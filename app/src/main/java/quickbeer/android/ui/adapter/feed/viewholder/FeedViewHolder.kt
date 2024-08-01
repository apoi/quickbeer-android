package quickbeer.android.ui.adapter.feed.viewholder

import androidx.annotation.CallSuper
import androidx.core.view.isVisible
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
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.ui.adapter.base.ScopeListViewHolder
import quickbeer.android.ui.adapter.feed.FeedListModel
import quickbeer.android.ui.transformations.ContainerLabelExtractor

abstract class FeedViewHolder(
    private val binding: FeedListItemBinding
) : ScopeListViewHolder<FeedListModel>(binding.root) {

    private var disposables = mutableListOf<Disposable>()

    @CallSuper
    protected open fun setUser(username: String) {
        binding.avatar.load(Constants.USER_AVATAR_PATH.format(username)) {
            crossfade(itemView.resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(CircleCropTransformation())
        }.let(disposables::add)
    }

    protected fun bindBeer(listModel: FeedListModel, scope: CoroutineScope) {
        scope.launch {
            listModel.getBeer(listModel.feedItem.beerId()).collect {
                it.valueOrNull()?.let { beer ->
                    withContext(Dispatchers.Main) {
                        setBeer(listModel.feedItem, beer)
                    }
                }
            }
        }
    }

    protected fun bindBrewer(listModel: FeedListModel, scope: CoroutineScope) {
        scope.launch {
            listModel.getBrewer(listModel.feedItem.brewerId()).collect {
                it.valueOrNull()?.let { brewer ->
                    withContext(Dispatchers.Main) {
                        setBrewer(listModel.feedItem, brewer)
                    }
                }
            }
        }
    }

    @CallSuper
    protected open fun setBeer(item: FeedItem, beer: Beer) {
        binding.background.load(beer.imageUri()) {
            crossfade(itemView.resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(
                ContainerLabelExtractor(LABEL_WIDTH, LABEL_HEIGHT),
                BlurTransformation(itemView.context, LABEL_BLUR)
            )
        }.let(disposables::add)
    }

    @CallSuper
    protected open fun setBrewer(item: FeedItem, brewer: Brewer) = Unit

    override fun unbind() {
        super.unbind()

        disposables.forEach(Disposable::dispose)
        disposables.clear()
        clear()
    }

    private fun clear() {
        binding.line1.text = ""
        binding.line2.text = ""
        binding.line3.text = ""

        binding.line1.isVisible = true
        binding.line2.isVisible = true
        binding.line3.isVisible = true

        binding.avatar.setImageResource(0)
        binding.background.setImageResource(0)
    }

    companion object {
        private const val LABEL_WIDTH = 500
        private const val LABEL_HEIGHT = 500
        private const val LABEL_BLUR = 15f
    }
}
