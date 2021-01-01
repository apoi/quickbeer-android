package quickbeer.android.domain.country.repository

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.data.state.State
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.store.CountryStore
import quickbeer.android.domain.countrylist.repository.CountryListRepository
import quickbeer.android.network.result.ApiResult

class CountryRepository(
    store: CountryStore,
    countryListRepository: CountryListRepository
) : DefaultRepository<Int, Country>(store, CountryFetcher(countryListRepository)::fetch)

/**
 * A fake fetcher using CountryListRepository for doing the fetch. This ensures the fetched country
 * list is persisted first. TODO this is a bit dirty, maybe some better approach?
 */
private class CountryFetcher(private val countryListRepository: CountryListRepository) {

    suspend fun fetch(key: Int): ApiResult<Country> {
        val state = countryListRepository.getStream(Accept())
            .filter { it !is State.Loading }
            .first()

        return when (state) {
            is State.Success -> ApiResult.Success(state.value.firstOrNull { it.id == key })
            is State.Empty -> ApiResult.Success(null)
            is State.Error -> ApiResult.UnknownError(state.cause)
            else -> error("Unexpected state $state")
        }
    }
}
