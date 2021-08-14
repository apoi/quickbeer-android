package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.first
import quickbeer.android.data.repository.Reject
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.TickedBeersFetcher
import quickbeer.android.domain.beerlist.store.TickedBeersStore
import quickbeer.android.domain.user.repository.UserRepository

class TickedBeersRepository @Inject constructor(
    store: TickedBeersStore,
    fetcher: TickedBeersFetcher,
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher) {

    class CurrentTickCountValidator(
        private val userId: Int,
        private val userRepository: UserRepository
    ) : Validator<List<Beer>> {

        /**
         * Tick data is likely valid if rate count in user's profile matches local count
         */
        override suspend fun validate(value: List<Beer>?): Boolean {
            if (value == null || value.isEmpty()) return false

            val result = userRepository.getStream(userId, Reject()).first()
            val rateCount = (result as? State.Success)?.value?.tickCount
            val localCount = value.size

            return rateCount == localCount
        }
    }
}
