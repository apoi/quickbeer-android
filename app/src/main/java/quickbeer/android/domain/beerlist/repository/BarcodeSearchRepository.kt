package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BarcodeSearchFetcher
import quickbeer.android.domain.beerlist.store.BarcodeSearchStore

class BarcodeSearchRepository @Inject constructor(
    override val store: BarcodeSearchStore,
    fetcher: BarcodeSearchFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher)
