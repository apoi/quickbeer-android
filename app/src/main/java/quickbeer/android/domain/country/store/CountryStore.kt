package quickbeer.android.domain.country.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.country.Country

class CountryStore(
    countryStoreCore: StoreCore<Int, Country>
) : DefaultStore<Int, Country>(countryStoreCore)
