package quickbeer.android.domain.countrylist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.idlist.IdList

class CountryListStore(
    indexStoreCore: StoreCore<String, IdList>,
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
