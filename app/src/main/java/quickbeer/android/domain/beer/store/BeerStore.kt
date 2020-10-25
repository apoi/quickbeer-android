package quickbeer.android.domain.beer.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.beer.Beer

class BeerStore(
    beerStoreCore: StoreCore<Int, Beer>
) : DefaultStore<Int, Beer>(beerStoreCore, Beer::id)
