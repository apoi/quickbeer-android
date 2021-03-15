package quickbeer.android.domain.reviewlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.reviewlist.network.BeerReviewsFetcher
import quickbeer.android.domain.reviewlist.network.BeerReviewsPageFetcher
import quickbeer.android.domain.reviewlist.store.BeerReviewsStore

class BeerReviewsRepository(
    store: BeerReviewsStore,
    fetcher: BeerReviewsFetcher,
    private val pageFetcher: BeerReviewsPageFetcher
) : ItemListRepository<String, Int, Review, ReviewJson>(store, fetcher) {

    // TODO persist result
    suspend fun fetch(beerId: String, page: Int) {
        pageFetcher.fetch(Pair(beerId, page))
    }
}
