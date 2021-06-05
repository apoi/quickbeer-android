package quickbeer.android.domain.country.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.country.Country

class CountryStore @Inject constructor(
    countryStoreCore: StoreCore<Int, Country>
) : DefaultStore<Int, Country>(countryStoreCore)
