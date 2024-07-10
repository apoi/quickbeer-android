package quickbeer.android.domain.rating.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao
import quickbeer.android.data.room.converter.ZonedDateTimeConverter

@Dao
@TypeConverters(ZonedDateTimeConverter::class)
abstract class RatingDao : CoreDao<Int, RatingEntity>(
    RatingEntity::id,
    RatingEntity.merger
) {

    @Query("SELECT * FROM reviews WHERE user_id=:userId AND beer_id=:beerId")
    abstract fun getRatingByUserStream(userId: Int, beerId: Int): Flow<RatingEntity?>

    @Query("SELECT * FROM reviews WHERE user_id=:userId")
    abstract fun getAllRatingsByUserStream(userId: Int): Flow<List<RatingEntity>>

    @Query("SELECT * FROM reviews WHERE id=:key")
    abstract suspend fun get(key: Int): RatingEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<RatingEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM reviews WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<RatingEntity>

    @Query("SELECT * FROM reviews WHERE id=:key")
    abstract fun getStream(key: Int): Flow<RatingEntity?>

    @Query("SELECT * FROM reviews")
    abstract suspend fun getAll(): List<RatingEntity>

    @Query("SELECT * FROM reviews")
    abstract fun getAllStream(): Flow<List<RatingEntity>>

    @Query("SELECT id FROM reviews")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM reviews")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: RatingEntity): RatingEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<RatingEntity>): List<RatingEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM reviews WHERE id=:key")
    abstract suspend fun delete(key: Int): Int

    @Query("DELETE FROM reviews")
    abstract suspend fun deleteAll(): Int
}
