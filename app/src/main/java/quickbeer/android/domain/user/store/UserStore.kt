package quickbeer.android.domain.user.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.store.SingleStore
import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.user.User

class UserStore @Inject constructor(
    private val userStoreCore: StoreCore<Int, User>
) : SingleStore<User?> {

    override suspend fun get(): User? {
        return userStoreCore.get(USER_KEY)
    }

    override fun getStream(): Flow<User?> {
        return userStoreCore.getStream(USER_KEY)
    }

    override suspend fun delete(): Boolean {
        return userStoreCore.delete(USER_KEY)
    }

    override suspend fun put(value: User?): Boolean {
        return if (value != null) {
            userStoreCore.put(USER_KEY, value) != null
        } else {
            delete()
        }
    }

    companion object {
        private const val USER_KEY = 0
    }
}
