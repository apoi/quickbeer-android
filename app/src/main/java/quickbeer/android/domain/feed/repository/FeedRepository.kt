package quickbeer.android.domain.feed.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.domain.feed.network.FeedItemJson
import quickbeer.android.domain.feed.network.FeedResourcesFetcher
import quickbeer.android.domain.feed.store.FeedStore

class FeedRepository @Inject constructor(
    override val store: FeedStore,
    fetcher: FeedResourcesFetcher
) : ItemListRepository<String, Int, FeedItem, FeedItemJson>(store, fetcher)
