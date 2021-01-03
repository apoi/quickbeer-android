package quickbeer.android.domain.brewer.store

import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.brewer.Brewer

class BrewerStore(
    brewerStoreCore: BrewerStoreCore
) : DefaultStore<Int, Brewer>(brewerStoreCore)
