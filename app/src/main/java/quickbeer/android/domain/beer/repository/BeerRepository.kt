package quickbeer.android.domain.beer.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerFetcher
import quickbeer.android.domain.beer.store.BeerStore

class BeerRepository @Inject constructor(
    store: BeerStore,
    fetcher: BeerFetcher,
) : DefaultRepository<Int, Beer>(
    store, fetcher::fetch
)
