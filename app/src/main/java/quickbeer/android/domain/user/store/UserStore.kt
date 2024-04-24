package quickbeer.android.domain.user.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.user.User

class UserStore @Inject constructor(
    private val userStoreCore: UserRoomCore
) : DefaultStore<Int, User>(userStoreCore) {

    suspend fun getCurrentUser(): User? {
        return userStoreCore.getCurrentUser()
    }

    fun getCurrentUserStream(): Flow<User?> {
        return userStoreCore.getCurrentUserStream()
    }
}
