package quickbeer.android.domain.user.repository

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.Constants
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.preferences.store.StringPreferenceStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.network.RateCountFetcher
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.result.ApiResult

class UserRepository @Inject constructor(
    private val store: UserStore,
    private val fetcher: RateCountFetcher,
    private val intPreferenceStore: IntPreferenceStore,
    private val stringPreferenceStore: StringPreferenceStore
) : SingleRepository<User?>() {

    private var job: Job? = null

    init {
        // User store doesn't persist, so init with locally stored user details.
        // Alternatively username could be fetched from backend on demand.
        job = CoroutineScope(Dispatchers.IO).launch {
            val userId = intPreferenceStore.get(Constants.USERID)
            val username = stringPreferenceStore.get(Constants.USERNAME)

            if (userId != null) {
                store.put(User(id = userId, username = username))
            }
        }
    }

    override suspend fun persist(value: User?) {
        store.put(value)
    }

    override suspend fun getLocal(): User? {
        return store.get()
    }

    override fun getLocalStream(): Flow<User?> {
        return store.getStream()
    }

    override suspend fun fetchRemote(): ApiResult<User?> {
        return intPreferenceStore.get(Constants.USERID)
            ?.let { id -> return fetcher.fetch(id) }
            ?: ApiResult.Success(null)
    }
}
