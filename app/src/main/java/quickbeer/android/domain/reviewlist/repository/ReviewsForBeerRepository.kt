package quickbeer.android.domain.reviewlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.reviewlist.ReviewPage
import quickbeer.android.domain.reviewlist.network.ReviewsForBeerFetcher
import quickbeer.android.domain.reviewlist.store.ReviewsForBeerStore

class ReviewsForBeerRepository(
    store: ReviewsForBeerStore,
    fetcher: ReviewsForBeerFetcher
) : ItemListRepository<ReviewPage, Int, Review, ReviewJson>(store, fetcher)
