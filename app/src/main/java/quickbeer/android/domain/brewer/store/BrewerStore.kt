package quickbeer.android.domain.brewer.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.brewer.Brewer

class BrewerStore(
    brewerStoreCore: StoreCore<Int, Brewer>
) : DefaultStore<Int, Brewer>(brewerStoreCore)
