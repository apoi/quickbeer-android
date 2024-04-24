package quickbeer.android.domain.ratinglist.repository

import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.store.BeerStore
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.network.UserRatingPageFetcher
import quickbeer.android.domain.ratinglist.store.UsersRatingStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.result.ApiResult
import quickbeer.android.network.result.map
import quickbeer.android.network.result.value

class UserRatingRepository @Inject constructor(
    private val userStore: UserStore,
    private val beerStore: BeerStore,
    private val ratingStore: UsersRatingStore,
    private val fetcher: UserRatingPageFetcher
) : SingleRepository<List<Rating>>() {

    override suspend fun persist(value: List<Rating>) {
        userStore.getCurrentUser()?.let { user ->
            ratingStore.put(user.id, value)
        }
    }

    override suspend fun getLocal(): List<Rating>? {
        return userStore.getCurrentUser()?.let { user ->
            ratingStore.get(user.id)
        }
    }

    override fun getLocalStream(): Flow<List<Rating>> {
        return userStore.getCurrentUserStream()
            .flatMapLatest { user ->
                if (user != null) {
                    ratingStore.getStream(user.id)
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override suspend fun fetchRemote(): ApiResult<List<Rating>> {
        return fetchRatings(userStore.getCurrentUser())
            .also { persistBeers(it) }
            .let(::takeRatings)
    }

    private suspend fun fetchRatings(user: User?): ApiResult<List<Pair<Beer, Rating>>> {
        if (user == null) error("User is null, can't fetch ratings")

        // The ratings API seems to be broken:
        //   1. It returns ticked beers in addition to rated beers, at least for a certain time span
        //   2. Result list is not sorted by the update timestamp
        //
        // This means that even though we cache the ratings, we can't say if
        //   1. Do we have all the ratings cached already
        //   2. Was some cached rating changed outside of the app
        //
        // Thus, we just need to fetch all the pages to get the full picture. We do that here by
        // calling the API recursively until we get an empty list or an error.
        return fetchPages(user, 1, emptyList())
    }

    private suspend fun fetchPages(
        user: User,
        page: Int,
        accumulator: List<Pair<Beer, Rating>>
    ): ApiResult<List<Pair<Beer, Rating>>> {
        val result = fetcher.fetch(Pair(user, page))

        return if (result is ApiResult.Success && !result.value.isNullOrEmpty()) {
            fetchPages(user, page + 1, accumulator + result.value)
        } else if (accumulator.isNotEmpty()) {
            ApiResult.Success(accumulator)
        } else {
            result
        }
    }

    private suspend fun persistBeers(result: ApiResult<List<Pair<Beer, Rating>>>) = coroutineScope {
        result.value()
            ?.map { (beer, _) -> async { beerStore.put(beer.id, beer) } }
            ?.awaitAll()
    }

    private fun takeRatings(result: ApiResult<List<Pair<Beer, Rating>>>): ApiResult<List<Rating>> {
        return result.map { list -> list.map(Pair<Beer, Rating>::second) }
    }
}
