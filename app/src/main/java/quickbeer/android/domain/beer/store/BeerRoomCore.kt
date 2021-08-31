package quickbeer.android.domain.beer.store

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.core.RoomDaoProxy
import quickbeer.android.data.store.core.RoomStoreCore
import quickbeer.android.domain.beer.Beer

class BeerRoomCore @Inject constructor(
    val database: Database
) : RoomStoreCore<Int, Beer, BeerEntity>(
    BeerEntityMapper,
    BeerDaoProxy(database.beerDao())
) {

    @Suppress("MagicNumber")
    fun search(query: String): Flow<List<Beer>> {
        val parts = query.split(" ")
        val tail = parts.drop(3)

        // Use three first terms directly in search, filter others programmatically
        return database.beerDao().search(parts.first(), parts.getOrNull(1), parts.getOrNull(2))
            .map { it.filter { entity -> contains(entity, tail) } }
            .map { it.map(BeerEntityMapper::mapTo) }
    }

    fun tickedBeers(): Flow<List<Beer>> {
        return database.beerDao().tickedBeers()
            .distinctUntilChanged()
            .map { it.map(BeerEntityMapper::mapTo) }
    }

    suspend fun clearTicks() {
        database.beerDao().clearTicks()
    }

    fun lastAccessed(): Flow<List<Int>> {
        return database.beerDao().lastAccessed()
    }

    private fun contains(entity: BeerEntity, queries: List<String>): Boolean {
        return queries.all { q -> entity.normalizedName?.contains(q) == true }
    }
}

private class BeerDaoProxy(
    private val dao: BeerDao,
) : RoomDaoProxy<Int, BeerEntity>() {

    override suspend fun get(key: Int) = dao.get(key)

    override suspend fun get(keys: List<Int>) = dao.get(keys)

    override fun getStream(key: Int) = dao.getStream(key).filterNotNull()

    override suspend fun getAll() = dao.getAll()

    override fun getAllStream() = dao.getAllStream()

    override suspend fun getKeys(): List<Int> = dao.getKeys()

    override fun getKeysStream(): Flow<List<Int>> = dao.getKeysStream()

    override suspend fun put(key: Int, value: BeerEntity) = dao.put(value)

    override suspend fun put(items: Map<Int, BeerEntity>) = dao.put(items.values.toList())

    override suspend fun delete(key: Int) = dao.delete(key) > 0
}
