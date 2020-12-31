package quickbeer.android.domain.stylelist.repository

import quickbeer.android.data.repository.repository.SingleItemListRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.stylelist.network.StyleJson
import quickbeer.android.domain.stylelist.network.StyleListFetcher
import quickbeer.android.domain.stylelist.store.StyleListStore

class StyleListRepository(
    store: StyleListStore,
    fetcher: StyleListFetcher
) : SingleItemListRepository<String, Int, Style, StyleJson>(store, fetcher)