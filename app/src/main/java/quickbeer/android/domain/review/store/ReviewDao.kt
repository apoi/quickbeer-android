package quickbeer.android.domain.review.store

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.room.CoreDao
import quickbeer.android.data.room.converter.ZonedDateTimeConverter
import quickbeer.android.domain.beer.store.BeerEntity

@Dao
@TypeConverters(ZonedDateTimeConverter::class)
abstract class ReviewDao : CoreDao<Int, ReviewEntity>(
    ReviewEntity::id,
    ReviewEntity.merger
) {

    @Query("SELECT * FROM reviews WHERE user_id=:userId")
    abstract fun reviewsForUser(userId: Int): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE id=:key")
    abstract suspend fun get(key: Int): ReviewEntity?

    @Transaction
    open suspend fun get(keys: List<Int>): List<ReviewEntity> {
        return getBatch(keys, ::getList)
    }

    @Query("SELECT * FROM reviews WHERE id IN (:keys)")
    abstract suspend fun getList(keys: List<Int>): List<ReviewEntity>

    @Query("SELECT * FROM reviews WHERE id=:key")
    abstract fun getStream(key: Int): Flow<ReviewEntity?>

    @Query("SELECT * FROM reviews")
    abstract suspend fun getAll(): List<ReviewEntity>

    @Query("SELECT * FROM reviews")
    abstract fun getAllStream(): Flow<List<ReviewEntity>>

    @Query("SELECT id FROM reviews")
    abstract fun getKeys(): List<Int>

    @Query("SELECT id FROM reviews")
    abstract fun getKeysStream(): Flow<List<Int>>

    @Transaction
    open suspend fun put(value: ReviewEntity): ReviewEntity? {
        return putMerged(value, ::get)
    }

    @Transaction
    open suspend fun put(values: List<ReviewEntity>): List<ReviewEntity> {
        return putBatch(values, ::get)
    }

    @Query("DELETE FROM reviews WHERE id=:key")
    abstract suspend fun delete(key: Int): Int
}
