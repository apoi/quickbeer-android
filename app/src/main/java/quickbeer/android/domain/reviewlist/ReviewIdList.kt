package quickbeer.android.domain.reviewlist

import quickbeer.android.data.repository.repository.ItemList

/**
 * Maps a paged review to a list of review IDs.
 */
typealias ReviewIdList = ItemList<ReviewPage, Int>
