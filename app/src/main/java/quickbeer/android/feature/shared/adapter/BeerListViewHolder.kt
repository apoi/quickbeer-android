package quickbeer.android.feature.shared.adapter

import android.annotation.SuppressLint
import com.squareup.picasso.Picasso
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quickbeer.android.data.state.State
import quickbeer.android.databinding.AlbumListItemBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.ui.adapter.simple.ScopeListViewHolder

class BeerListViewHolder(
    private val binding: AlbumListItemBinding
) : ScopeListViewHolder<BeerListModel>(binding.root) {

    override fun bind(item: BeerListModel, scope: CoroutineScope) {
        scope.launch {
            item.getBeer().collect {
                withContext(Dispatchers.Main) { updated(it) }
            }
        }
    }

    override fun unbind() {
        super.unbind()

        Picasso.get().cancelRequest(binding.listItemPhoto)
        binding.listItemPhoto.setImageDrawable(null)
        binding.listItemTitle.text = null
        binding.listItemDescription.text = null
    }

    private fun updated(state: State<Beer>) {
        when (state) {
            is State.Success -> setBeer(state.value)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBeer(beer: Beer) {
        Picasso.get()
            .load(beer.imageUri())
            .fit()
            .centerCrop()
            .into(binding.listItemPhoto)

        binding.listItemTitle.text = "Beer id: ${beer.id}"
        binding.listItemDescription.text = beer.name?.capitalize(Locale.ROOT)
    }
}
