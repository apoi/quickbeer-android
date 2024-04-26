package quickbeer.android.domain.user.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.user.User

class UserRoomCore @Inject constructor(
    private val database: Database
) : RoomStoreCore<Int, User, UserEntity>(
    UserEntityMapper,
    UserDaoProxy(database.userDao())
) {

    suspend fun getCurrentUser(): User? {
        return database.userDao().getCurrentUser()
            .firstOrNull()
            ?.firstOrNull()
            ?.let(UserEntityMapper::mapTo)
    }

    fun getCurrentUserStream(): Flow<User?> {
        return database.userDao().getCurrentUser()
            .map { it.firstOrNull() }
            .map { it?.let(UserEntityMapper::mapTo) }
    }
}

private class UserDaoProxy(
    private val dao: UserDao
) : RoomDaoProxy<Int, UserEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key).filterNotNull()

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: UserEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, UserEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0
}
