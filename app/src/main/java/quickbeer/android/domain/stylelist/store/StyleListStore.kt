package quickbeer.android.domain.stylelist.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.style.Style
import quickbeer.android.inject.IdListPersistedCore

class StyleListStore @Inject constructor(
    @IdListPersistedCore indexStoreCore: StoreCore<String, IdList>,
    styleStoreCore: StoreCore<Int, Style>
) : SingleItemListStore<String, Int, Style>(
    KEY,
    getKey = Style::id,
    indexCore = indexStoreCore,
    valueCore = styleStoreCore
) {

    companion object {
        private const val KEY = "styles"
    }
}
