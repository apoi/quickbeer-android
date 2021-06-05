package quickbeer.android.domain.beer.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.beer.Beer

class BeerStore @Inject constructor(
    beerStoreCore: BeerStoreCore
) : DefaultStore<Int, Beer>(beerStoreCore)
