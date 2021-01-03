package quickbeer.android.domain.beer.store

import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.beer.Beer

class BeerStore(
    beerStoreCore: BeerStoreCore
) : DefaultStore<Int, Beer>(beerStoreCore)
