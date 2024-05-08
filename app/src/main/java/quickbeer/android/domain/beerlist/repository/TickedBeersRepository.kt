package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.SingleRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beerlist.network.TickedBeersFetcher
import quickbeer.android.domain.beerlist.store.TickedBeersStore
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.result.ApiResult

class TickedBeersRepository @Inject constructor(
    private val store: TickedBeersStore,
    private val userStore: UserStore,
    private val fetcher: TickedBeersFetcher
) : SingleRepository<List<Beer>>() {

    override suspend fun getLocal(): List<Beer> {
        return store.get()
    }

    override fun getLocalStream(): Flow<List<Beer>> {
        return store.getStream()
    }

    override suspend fun persist(value: List<Beer>) {
        store.put(value)
    }

    override suspend fun fetchRemote(): ApiResult<List<Beer>> {
        val user = userStore.getCurrentUser() ?: error("User is null, can't fetch ticks")
        return fetcher.fetch(user.id.toString())
    }
}
