package quickbeer.android.ui.adapter.rating

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.RatingListItemBinding
import quickbeer.android.feature.beerdetails.BeerRatingsViewHolder
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class RatingTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.rating_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return BeerRatingsViewHolder(createBinding(RatingListItemBinding::inflate, parent))
    }
}
