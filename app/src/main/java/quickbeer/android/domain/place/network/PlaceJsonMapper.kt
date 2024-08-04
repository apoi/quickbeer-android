package quickbeer.android.domain.place.network

import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.place.Place
import quickbeer.android.util.JsonMapper

object PlaceJsonMapper : JsonMapper<Int, Place, PlaceJson> {

    override fun map(key: Int, source: PlaceJson): Place {
        return Place(
            id = source.id,
            updated = ZonedDateTime.now(),
            accessed = null
        )
    }
}
