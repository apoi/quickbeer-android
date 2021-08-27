package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.TickedBeersFetcher
import quickbeer.android.domain.beerlist.store.TickedBeersStore
import quickbeer.android.domain.user.store.UserStore

class TickedBeersRepository @Inject constructor(
    store: TickedBeersStore,
    fetcher: TickedBeersFetcher,
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher) {

    class TickCountValidator(
        private val userId: Int,
        private val userStore: UserStore,
    ) : Validator<List<Beer>> {

        /**
         * Tick data is likely valid if rate count in user's profile matches local count
         */
        override suspend fun validate(value: List<Beer>?): Boolean {
            if (value == null || value.isEmpty()) return false

            val localCount = userStore.get(userId)?.tickCount
            return localCount == null || value.size > localCount
        }
    }
}
