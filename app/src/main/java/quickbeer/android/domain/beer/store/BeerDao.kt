package quickbeer.android.domain.beer.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao
import quickbeer.android.data.room.converter.ZonedDateTimeConverter

@Dao
@TypeConverters(ZonedDateTimeConverter::class)
abstract class BeerDao : CoreDao<Int, BeerEntity>(
    BeerEntity::id,
    BeerEntity.merger
) {

    @Query(
        """SELECT * FROM beers
        WHERE instr(normalized_name, :q1) > 0
        AND (:q2 IS NULL OR instr(normalized_name, :q2) > 0)
        AND (:q3 IS NULL OR instr(normalized_name, :q3) > 0)"""
    )
    abstract fun search(
        q1: String,
        q2: String? = null,
        q3: String? = null
    ): Flow<List<BeerEntity>>

    @Query("SELECT * FROM beers WHERE liked > 0 ORDER BY time_entered")
    abstract fun tickedBeers(): Flow<List<BeerEntity>>

    @Query("UPDATE beers SET liked = null, time_entered = null")
    abstract suspend fun clearTicks()

    @Query("SELECT id FROM beers WHERE accessed IS NOT NULL ORDER BY accessed DESC")
    abstract fun lastAccessed(): Flow<List<Int>>

    @Query("SELECT * FROM beers WHERE id=:key")
    abstract suspend fun get(key: Int): BeerEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<BeerEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM beers WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<BeerEntity>

    @Query("SELECT * FROM beers WHERE id=:key")
    abstract fun getStream(key: Int): Flow<BeerEntity?>

    @Query("SELECT * FROM beers")
    abstract suspend fun getAll(): List<BeerEntity>

    @Query("SELECT * FROM beers")
    abstract fun getAllStream(): Flow<List<BeerEntity>>

    @Query("SELECT id FROM beers")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM beers")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: BeerEntity): BeerEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<BeerEntity>): List<BeerEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM beers WHERE id=:key")
    abstract suspend fun delete(key: Int): Int
}
