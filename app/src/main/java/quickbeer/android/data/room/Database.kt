package quickbeer.android.data.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import quickbeer.android.domain.beer.store.BeerDao
import quickbeer.android.domain.beer.store.BeerEntity
import quickbeer.android.domain.brewer.store.BrewerDao
import quickbeer.android.domain.brewer.store.BrewerEntity
import quickbeer.android.domain.idlist.store.IdListDao
import quickbeer.android.domain.idlist.store.IdListEntity
import quickbeer.android.domain.place.store.PlaceDao
import quickbeer.android.domain.place.store.PlaceEntity
import quickbeer.android.domain.rating.store.RatingDao
import quickbeer.android.domain.rating.store.RatingEntity
import quickbeer.android.domain.style.store.StyleDao
import quickbeer.android.domain.style.store.StyleEntity
import quickbeer.android.domain.user.store.UserDao
import quickbeer.android.domain.user.store.UserEntity

const val DATABASE_NAME = "quickbeer4.db"
const val BATCH_SIZE = 999

private const val CURRENT_VERSION = 3

@Database(
    version = CURRENT_VERSION,
    entities = [
        IdListEntity::class,
        BeerEntity::class,
        BrewerEntity::class,
        RatingEntity::class,
        StyleEntity::class,
        UserEntity::class,
        PlaceEntity::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class Database : RoomDatabase() {

    abstract fun idListDao(): IdListDao

    abstract fun beerDao(): BeerDao

    abstract fun brewerDao(): BrewerDao

    abstract fun ratingDao(): RatingDao

    abstract fun styleDao(): StyleDao

    abstract fun userDao(): UserDao

    abstract fun placeDao(): PlaceDao
}
