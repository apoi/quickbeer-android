package quickbeer.android.domain.style.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao

@Dao
abstract class StyleDao : CoreDao<Int, StyleEntity>(
    StyleEntity::id,
    StyleEntity.merger
) {

    @Query("SELECT * FROM styles WHERE id=:key")
    abstract suspend fun get(key: Int): StyleEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<StyleEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM styles WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<StyleEntity>

    @Query("SELECT * FROM styles WHERE id=:key")
    abstract fun getStream(key: Int): Flow<StyleEntity?>

    @Query("SELECT * FROM styles")
    abstract suspend fun getAll(): List<StyleEntity>

    @Query("SELECT * FROM styles")
    abstract fun getAllStream(): Flow<List<StyleEntity>>

    @Query("SELECT id FROM styles")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM styles")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: StyleEntity): StyleEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<StyleEntity>): List<StyleEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM styles WHERE id=:key")
    abstract suspend fun delete(key: Int): Int

    @Query("DELETE FROM styles")
    abstract suspend fun deleteAll(): Int
}
