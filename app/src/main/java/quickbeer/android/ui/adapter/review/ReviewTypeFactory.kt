package quickbeer.android.ui.adapter.review

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.ReviewListItemBinding
import quickbeer.android.feature.beerdetails.BeerReviewsViewHolder
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class ReviewTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.review_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return BeerReviewsViewHolder(createBinding(ReviewListItemBinding::inflate, parent))
    }
}
