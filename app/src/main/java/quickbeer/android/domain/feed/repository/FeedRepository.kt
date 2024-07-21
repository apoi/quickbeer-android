package quickbeer.android.domain.feed.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.feed.FeedDataItem
import quickbeer.android.domain.feed.network.FeedFetcher
import quickbeer.android.domain.feed.network.FeedItemJson
import quickbeer.android.domain.feed.store.FeedStore

class FeedRepository @Inject constructor(
    override val store: FeedStore,
    fetcher: FeedFetcher
) : ItemListRepository<String, Int, FeedDataItem, FeedItemJson>(store, fetcher)
