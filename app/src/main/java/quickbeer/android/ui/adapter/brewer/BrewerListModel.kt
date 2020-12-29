package quickbeer.android.ui.adapter.brewer

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.state.State
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory

class BrewerListModel(
    val id: Int,
    private val repository: BrewerRepository
) : ListItem {

    override fun id(): Long {
        return id.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }

    fun getBrewer(): Flow<State<Brewer>> {
        return repository.getStream(id, Brewer.BasicDataValidator())
    }
}
