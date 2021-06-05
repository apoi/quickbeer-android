package quickbeer.android.domain.style.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.style.Style

class StyleStore @Inject constructor(
    styleStoreCore: StoreCore<Int, Style>
) : DefaultStore<Int, Style>(styleStoreCore)
