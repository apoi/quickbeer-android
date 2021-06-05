package quickbeer.android.domain.brewer.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.brewer.Brewer

class BrewerStore @Inject constructor(
    brewerStoreCore: BrewerStoreCore
) : DefaultStore<Int, Brewer>(brewerStoreCore)
