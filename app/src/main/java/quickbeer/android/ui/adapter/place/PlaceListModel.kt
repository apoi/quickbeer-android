package quickbeer.android.ui.adapter.place

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.repository.PlaceRepository
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class PlaceListModel(
    val placeId: Int,
    private val placeRepository: PlaceRepository
) : ListItem {

    override fun id(): Long {
        return placeId.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }

    fun getPlace(brewerId: Int): Flow<State<Place>> {
        return placeRepository.getStream(brewerId, Accept())
    }
}
