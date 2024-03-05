package quickbeer.android.domain.ratinglist.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.network.UsersRatingsPageFetcher
import quickbeer.android.domain.ratinglist.store.UsersRatingsStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.result.ApiResult
import timber.log.Timber

class UserRatingsRepository @Inject constructor(
    private val store: UsersRatingsStore,
    private val fetcher: UsersRatingsPageFetcher,
    private val intPreferenceStore: IntPreferenceStore,
    private val userStore: UserStore,
) : SingleRepository<List<Rating>>() {

    private suspend fun getUserId(): Int {
        return intPreferenceStore.get(LoginManager.USERID)
            ?: error("User is not logged in")
    }

    private fun getUserIdStream(): Flow<Int?> {
        return intPreferenceStore.getKeysStream()
            .map { intPreferenceStore.get(LoginManager.USERID) }
            .distinctUntilChanged()
    }

    override suspend fun persist(value: List<Rating>) {
        store.put(getUserId(), value)
    }

    override suspend fun getLocal(): List<Rating>? {
        return store.get(getUserId())
    }

    override fun getLocalStream(): Flow<List<Rating>> {
        return getUserIdStream()
            .flatMapLatest { userId ->
                if (userId != null) store.getStream(userId)
                else flowOf(emptyList())
            }
    }

    override suspend fun fetchRemote(): ApiResult<List<Rating>> {
        return fetchRatings(userStore.get(getUserId()))
    }

    private suspend fun fetchRatings(user: User?): ApiResult<List<Rating>> {
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
        accumulator: List<Rating>
    ): ApiResult<List<Rating>> {
        Timber.d("QUICKBEER: FETCHING PAGE $page")
        val result = fetcher.fetch(Pair(user, page))

        return if (result is ApiResult.Success && !result.value.isNullOrEmpty()) {
            fetchPages(user, page + 1, accumulator + result.value)
        } else if (accumulator.isNotEmpty()) {
            Timber.d("QUICKBEER: RECURSION DONE, GOT ${accumulator.size} RATINGS")
            ApiResult.Success(accumulator)
        } else {
            result
        }
    }

    private fun mergeApiResults(results: List<ApiResult<List<Rating>>>): ApiResult<List<Rating>> {
        Timber.d("QUICKBEER: MERGING ${results.size} PAGES")
        Timber.d("QUICKBEER: MERGING $results")

        return results.reduce { acc: ApiResult<List<Rating>>, result: ApiResult<List<Rating>> ->
            when {
                acc is ApiResult.Success && result is ApiResult.Success -> {
                    // If both are Success, combine the contained lists
                    ApiResult.Success(acc.value.orEmpty() + result.value.orEmpty())
                }
                acc !is ApiResult.Success -> {
                    // Accumulator is an error, return it (first element error case)
                    return acc
                }
                else -> {
                    // Current is an error, return it (stops further reduction)
                    return result
                }
            }
        }
    }
}
