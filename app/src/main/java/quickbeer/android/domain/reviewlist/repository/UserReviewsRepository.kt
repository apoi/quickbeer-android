package quickbeer.android.domain.reviewlist.repository

import javax.inject.Inject
import kotlin.math.ceil
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.reviewlist.network.UsersReviewsPageFetcher
import quickbeer.android.domain.reviewlist.store.UsersReviewsStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.result.ApiResult
import timber.log.Timber

private const val RATINGS_PER_PAGE = 100

class UserReviewsRepository @Inject constructor(
    private val store: UsersReviewsStore,
    private val fetcher: UsersReviewsPageFetcher,
    private val intPreferenceStore: IntPreferenceStore,
    private val userStore: UserStore,
) : SingleRepository<List<Review>>() {

    private suspend fun getUserId(): Int {
        return intPreferenceStore.get(LoginManager.USERID)
            ?: error("User is not logged in")
    }

    private fun getUserIdStream(): Flow<Int?> {
        return intPreferenceStore.getKeysStream()
            .map { intPreferenceStore.get(LoginManager.USERID) }
            .distinctUntilChanged()
    }

    override suspend fun persist(value: List<Review>) {
        store.put(getUserId(), value)
    }

    override suspend fun getLocal(): List<Review>? {
        return store.get(getUserId())
    }

    override fun getLocalStream(): Flow<List<Review>> {
        return getUserIdStream()
            .flatMapLatest { userId ->
                if (userId != null) store.getStream(userId)
                else flowOf(emptyList())
            }
    }

    override suspend fun fetchRemote(): ApiResult<List<Review>> {
        return fetchReviews(userStore.get(getUserId()))
    }

    private suspend fun fetchReviews(user: User?): ApiResult<List<Review>> {
        if (user == null) error("User is null, can't fetch reviews")

        val reviews = user.rateCount ?: 0
        val pages = ceil(reviews.toDouble() / RATINGS_PER_PAGE).toInt()

        Timber.d("QUICKBEER: FETCHING $pages PAGES")

        val results = (0..pages).map { page ->
            coroutineScope {
                async { fetcher.fetch(Pair(user, page)) }
            }
        }.awaitAll()

        return mergeApiResults(results)
    }

    private fun mergeApiResults(results: List<ApiResult<List<Review>>>): ApiResult<List<Review>> {
        Timber.d("QUICKBEER: MERGING ${results.size} PAGES")
        Timber.d("QUICKBEER: MERGING $results")

        return results.reduce { acc: ApiResult<List<Review>>, result: ApiResult<List<Review>> ->
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
