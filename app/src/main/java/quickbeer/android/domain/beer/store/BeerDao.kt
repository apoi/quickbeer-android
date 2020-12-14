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

    @Query("SELECT * FROM beers WHERE id=:key")
    abstract suspend fun get(key: Int): BeerEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<BeerEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM beers WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<BeerEntity>

    @Query("SELECT * FROM beers WHERE id=:key")
    abstract fun getStream(key: Int): Flow<BeerEntity>

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
