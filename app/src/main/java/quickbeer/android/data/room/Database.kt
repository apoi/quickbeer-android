package quickbeer.android.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import quickbeer.android.domain.beer.store.BeerDao
import quickbeer.android.domain.beer.store.BeerEntity
import quickbeer.android.domain.idlist.store.IdListDao
import quickbeer.android.domain.idlist.store.IdListEntity

const val DATABASE_NAME = "quickbeer.db"
const val BATCH_SIZE = 999

private const val CURRENT_VERSION = 1

@Database(entities = [IdListEntity::class, BeerEntity::class], version = CURRENT_VERSION)
abstract class Database : RoomDatabase() {

    abstract fun idListDao(): IdListDao

    abstract fun beerDao(): BeerDao
}
