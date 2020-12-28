package quickbeer.android.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import quickbeer.android.domain.beer.store.BeerDao
import quickbeer.android.domain.beer.store.BeerEntity
import quickbeer.android.domain.brewer.store.BrewerDao
import quickbeer.android.domain.brewer.store.BrewerEntity
import quickbeer.android.domain.idlist.store.IdListDao
import quickbeer.android.domain.idlist.store.IdListEntity
import quickbeer.android.domain.style.store.StyleDao
import quickbeer.android.domain.style.store.StyleEntity

const val DATABASE_NAME = "quickbeer.db"
const val BATCH_SIZE = 999

private const val CURRENT_VERSION = 1

@Database(
    entities = [
        IdListEntity::class,
        BeerEntity::class,
        BrewerEntity::class,
        StyleEntity::class
    ],
    version = CURRENT_VERSION
)
abstract class Database : RoomDatabase() {

    abstract fun idListDao(): IdListDao

    abstract fun beerDao(): BeerDao

    abstract fun brewerDao(): BrewerDao

    abstract fun styleDao(): StyleDao
}
