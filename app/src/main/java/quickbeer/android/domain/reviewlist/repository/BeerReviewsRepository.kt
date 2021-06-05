package quickbeer.android.domain.reviewlist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.reviewlist.network.BeerReviewsFetcher
import quickbeer.android.domain.reviewlist.network.BeerReviewsPageFetcher
import quickbeer.android.domain.reviewlist.store.BeerReviewsStore
import quickbeer.android.network.result.ApiResult

class BeerReviewsRepository @Inject constructor(
    store: BeerReviewsStore,
    fetcher: BeerReviewsFetcher,
    private val pageFetcher: BeerReviewsPageFetcher
) : ItemListRepository<String, Int, Review, ReviewJson>(store, fetcher) {

    suspend fun fetch(beerId: String, page: Int) {
        val response = pageFetcher.fetch(Pair(beerId, page))

        if (response is ApiResult.Success) {
            if (response.value != null) {
                // Response is persisted to be emitted later
                persist(beerId, response.value)
            }
        }
    }
}
