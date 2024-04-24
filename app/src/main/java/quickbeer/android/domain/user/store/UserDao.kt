package quickbeer.android.domain.user.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao

@Dao
abstract class UserDao : CoreDao<Int, UserEntity>(
    UserEntity::id,
    UserEntity.merger
) {

    @Query(
        """SELECT * FROM users
        WHERE logged_in=1
        AND username IS NOT NULL
        AND password IS NOT NULL"""
    )
    abstract fun getCurrentUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id=:key")
    abstract suspend fun get(key: Int): UserEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<UserEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM users WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<UserEntity>

    @Query("SELECT * FROM users WHERE id=:key")
    abstract fun getStream(key: Int): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    abstract suspend fun getAll(): List<UserEntity>

    @Query("SELECT * FROM users")
    abstract fun getAllStream(): Flow<List<UserEntity>>

    @Query("SELECT id FROM users")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM users")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: UserEntity): UserEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<UserEntity>): List<UserEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM users WHERE id=:key")
    abstract suspend fun delete(key: Int): Int
}
