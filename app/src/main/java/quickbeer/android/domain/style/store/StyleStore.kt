package quickbeer.android.domain.style.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.style.Style

class StyleStore(
    styleStoreCore: StoreCore<Int, Style>
) : DefaultStore<Int, Style>(styleStoreCore)
