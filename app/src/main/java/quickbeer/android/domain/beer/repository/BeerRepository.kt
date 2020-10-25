package quickbeer.android.domain.beer.repository

import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerFetcher
import quickbeer.android.domain.beer.store.BeerStore

class BeerRepository(
    store: BeerStore,
    fetcher: BeerFetcher
) : DefaultRepository<Int, Beer>(
    store, fetcher::fetch
)
