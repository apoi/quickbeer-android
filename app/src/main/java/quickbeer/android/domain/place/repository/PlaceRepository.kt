package quickbeer.android.domain.place.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.network.PlaceFetcher
import quickbeer.android.domain.place.store.PlaceStore

class PlaceRepository @Inject constructor(
    store: PlaceStore,
    fetcher: PlaceFetcher
) : DefaultRepository<Int, Place>(store, fetcher::fetch)
