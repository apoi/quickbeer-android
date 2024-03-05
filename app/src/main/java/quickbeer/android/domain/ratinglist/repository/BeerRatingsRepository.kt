package quickbeer.android.domain.ratinglist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.RatingJson
import quickbeer.android.domain.ratinglist.network.BeerRatingsFetcher
import quickbeer.android.domain.ratinglist.network.BeerRatingsPageFetcher
import quickbeer.android.domain.ratinglist.store.BeerRatingsStore
import quickbeer.android.network.result.ApiResult

class BeerRatingsRepository @Inject constructor(
    store: BeerRatingsStore,
    fetcher: BeerRatingsFetcher,
    private val pageFetcher: BeerRatingsPageFetcher
) : ItemListRepository<String, Int, Rating, RatingJson>(store, fetcher) {

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
