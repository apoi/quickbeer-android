package quickbeer.android.domain.countrylist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.countrylist.network.CountryJson
import quickbeer.android.domain.countrylist.network.CountryListFetcher
import quickbeer.android.domain.countrylist.store.CountryListStore

class CountryListRepository @Inject constructor(
    store: CountryListStore,
    fetcher: CountryListFetcher
) : SingleItemListRepository<String, Int, Country, CountryJson>(store, fetcher)
