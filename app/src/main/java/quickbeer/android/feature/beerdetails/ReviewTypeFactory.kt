package quickbeer.android.feature.beerdetails

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.BeerDetailsReviewBinding
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory
import quickbeer.android.ui.adapter.simple.ListViewHolder

class ReviewTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return when (item) {
            is ReviewModel -> R.layout.beer_details_review
            else -> error("Invalid item")
        }
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return when (type) {
            R.layout.beer_details_review -> {
                BeerReviewsViewHolder(createBinding(BeerDetailsReviewBinding::inflate, parent))
            }
            else -> error("Invalid type")
        }
    }
}
