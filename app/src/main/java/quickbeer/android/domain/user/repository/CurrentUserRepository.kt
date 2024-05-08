package quickbeer.android.domain.user.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.network.RateCountFetcher
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.result.ApiResult

class CurrentUserRepository @Inject constructor(
    private val store: UserStore,
    private val fetcher: RateCountFetcher
) : SingleRepository<User>() {

    override suspend fun persist(value: User) {
        store.put(value.id, value)
    }

    override suspend fun getLocal(): User? {
        return store.getCurrentUser()
    }

    override fun getLocalStream(): Flow<User?> {
        return store.getCurrentUserStream()
    }

    override suspend fun fetchRemote(): ApiResult<User> {
        return getLocal()
            ?.let { user -> return fetcher.fetch(user.id) }
            ?: ApiResult.Success(null)
    }
}
