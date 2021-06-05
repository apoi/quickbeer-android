package quickbeer.android.domain.brewer.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerFetcher
import quickbeer.android.domain.brewer.store.BrewerStore

class BrewerRepository @Inject constructor(
    store: BrewerStore,
    fetcher: BrewerFetcher
) : DefaultRepository<Int, Brewer>(store, fetcher::fetch)
