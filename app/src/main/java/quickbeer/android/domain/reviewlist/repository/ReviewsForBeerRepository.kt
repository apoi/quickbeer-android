package quickbeer.android.domain.reviewlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Review
import quickbeer.android.domain.beer.network.ReviewJson
import quickbeer.android.domain.beerlist.network.ReviewsInStyleFetcher
import quickbeer.android.domain.beerlist.store.ReviewsInStyleStore

class ReviewsForBeerRepository(
    store: ReviewsInStyleStore,
    fetcher: ReviewsInStyleFetcher
) : ItemListRepository<String, Int, Review, ReviewJson>(store, fetcher)
