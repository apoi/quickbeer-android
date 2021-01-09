package quickbeer.android.feature.beerdetails

import quickbeer.android.domain.review.Review
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class ReviewModel(
    val review: Review
) : ListItem {

    override fun id(): Long {
        return review.id.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }
}
