package quickbeer.android.ui.adapter.beer

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory

class BeerListModel(
    val id: Int,
    private val repository: BeerRepository
) : ListItem {

    override fun id(): Long {
        return id.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }

    fun getBeer(): Flow<State<Beer>> {
        return repository.getStream(id, Beer.BasicDataValidator())
    }
}
