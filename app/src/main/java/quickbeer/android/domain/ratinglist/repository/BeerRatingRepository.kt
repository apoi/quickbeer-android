package quickbeer.android.domain.ratinglist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.BeerRatingJson
import quickbeer.android.domain.ratinglist.network.BeerRatingFetcher
import quickbeer.android.domain.ratinglist.network.BeerRatingPageFetcher
import quickbeer.android.domain.ratinglist.store.BeerRatingStore
import quickbeer.android.network.result.ApiResult

class BeerRatingRepository @Inject constructor(
    store: BeerRatingStore,
    fetcher: BeerRatingFetcher,
    private val pageFetcher: BeerRatingPageFetcher
) : ItemListRepository<String, Int, Rating, BeerRatingJson>(store, fetcher) {

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
