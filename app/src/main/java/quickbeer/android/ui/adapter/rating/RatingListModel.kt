package quickbeer.android.ui.adapter.rating

import quickbeer.android.domain.rating.Rating
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class RatingListModel(
    val rating: Rating
) : ListItem {

    override fun id(): Long {
        return rating.id.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }
}
