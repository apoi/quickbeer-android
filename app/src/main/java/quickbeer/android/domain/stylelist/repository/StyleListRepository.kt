package quickbeer.android.domain.stylelist.repository

import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.network.StyleJson
import quickbeer.android.domain.stylelist.network.StyleListFetcher
import quickbeer.android.domain.stylelist.store.StyleListStore
import quickbeer.android.network.RateBeerApi

class StyleListRepository(
    store: StyleListStore,
    api: RateBeerApi
) : SingleItemListRepository<String, Int, Style, StyleJson>(
    store, StyleListFetcher(api)
)
