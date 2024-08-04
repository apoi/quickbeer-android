package quickbeer.android.domain.place.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao
import quickbeer.android.data.room.converter.ZonedDateTimeConverter

@Dao
@TypeConverters(ZonedDateTimeConverter::class)
abstract class PlaceDao : CoreDao<Int, PlaceEntity>(
    PlaceEntity::id,
    PlaceEntity.merger
) {

    @Query(
        """SELECT * FROM places
        WHERE instr(normalized_name, :q1) > 0
        AND (:q2 IS NULL OR instr(normalized_name, :q2) > 0)
        AND (:q3 IS NULL OR instr(normalized_name, :q3) > 0)"""
    )
    abstract fun search(
        q1: String,
        q2: String? = null,
        q3: String? = null
    ): Flow<List<PlaceEntity>>

    @Query("SELECT id FROM places WHERE accessed IS NOT NULL ORDER BY accessed DESC")
    abstract fun lastAccessed(): Flow<List<Int>>

    @Query("SELECT * FROM places WHERE id=:key")
    abstract suspend fun get(key: Int): PlaceEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<PlaceEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM places WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id=:key")
    abstract fun getStream(key: Int): Flow<PlaceEntity?>

    @Query("SELECT * FROM places")
    abstract suspend fun getAll(): List<PlaceEntity>

    @Query("SELECT * FROM places")
    abstract fun getAllStream(): Flow<List<PlaceEntity>>

    @Query("SELECT id FROM places")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM places")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: PlaceEntity): PlaceEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<PlaceEntity>): List<PlaceEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM places WHERE id=:key")
    abstract suspend fun delete(key: Int): Int

    @Query("DELETE FROM places")
    abstract suspend fun deleteAll(): Int
}
