package quickbeer.android.domain.idlist.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao
import quickbeer.android.data.room.converter.IntListConverter

@Dao
@TypeConverters(IntListConverter::class)
abstract class IdListDao : CoreDao<String, IdListEntity>(
    IdListEntity::id,
    IdListEntity.merger
) {

    @Query("SELECT * FROM lists WHERE id=:key")
    abstract suspend fun get(key: String): IdListEntity?

    @Transaction
    open suspend fun get(keys: List<String>): List<IdListEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM lists WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<String>): List<IdListEntity>

    @Query("SELECT * FROM lists WHERE id=:key")
    abstract fun getStream(key: String): Flow<IdListEntity>

    @Query("SELECT * FROM lists")
    abstract suspend fun getAll(): List<IdListEntity>

    @Query("SELECT * FROM lists")
    abstract fun getAllStream(): Flow<List<IdListEntity>>

    @Transaction
    open suspend fun put(value: IdListEntity): IdListEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<IdListEntity>): List<IdListEntity> {
        return putBatch(values, ::getList)
    }

    @Query("DELETE FROM lists WHERE id=:key")
    abstract suspend fun delete(key: String): Int
}