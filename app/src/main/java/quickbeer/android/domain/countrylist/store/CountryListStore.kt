package quickbeer.android.domain.countrylist.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.inject.IdListPersistedCore

class CountryListStore @Inject constructor(
    @IdListPersistedCore indexStoreCore: StoreCore<String, IdList>,
    countryStoreCore: StoreCore<Int, Country>
) : SingleItemListStore<String, Int, Country>(
    KEY,
    getKey = Country::id,
    indexCore = indexStoreCore,
    valueCore = countryStoreCore
) {

    companion object {
        private const val KEY = "countries"
    }
}
