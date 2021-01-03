package quickbeer.android.domain.brewer.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao
import quickbeer.android.data.room.converter.ZonedDateTimeConverter

@Dao
@TypeConverters(ZonedDateTimeConverter::class)
abstract class BrewerDao : CoreDao<Int, BrewerEntity>(
    BrewerEntity::id,
    BrewerEntity.merger
) {

    @Query(
        """SELECT * FROM brewers
        WHERE instr(normalized_name, :q1) > 0
        AND (:q2 IS NULL OR instr(normalized_name, :q2) > 0)
        AND (:q3 IS NULL OR instr(normalized_name, :q3) > 0)"""
    )
    abstract fun search(
        q1: String,
        q2: String? = null,
        q3: String? = null
    ): Flow<List<BrewerEntity>>

    @Query("SELECT * FROM brewers WHERE id=:key")
    abstract suspend fun get(key: Int): BrewerEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<BrewerEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM brewers WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<BrewerEntity>

    @Query("SELECT * FROM brewers WHERE id=:key")
    abstract fun getStream(key: Int): Flow<BrewerEntity>

    @Query("SELECT * FROM brewers")
    abstract suspend fun getAll(): List<BrewerEntity>

    @Query("SELECT * FROM brewers")
    abstract fun getAllStream(): Flow<List<BrewerEntity>>

    @Query("SELECT id FROM brewers")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM brewers")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: BrewerEntity): BrewerEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<BrewerEntity>): List<BrewerEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM brewers WHERE id=:key")
    abstract suspend fun delete(key: Int): Int
}
