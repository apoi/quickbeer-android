package quickbeer.android.domain.place.store

import quickbeer.android.domain.place.Place
import quickbeer.android.util.Mapper

object PlaceEntityMapper : Mapper<Place, PlaceEntity> {

    override fun mapFrom(source: Place): PlaceEntity {
        return PlaceEntity(
            id = source.id,
            name = source.name,
            normalizedName = source.normalizedName,
            updated = source.updated,
            accessed = source.accessed
        )
    }

    override fun mapTo(source: PlaceEntity): Place {
        return Place(
            id = source.id,
            name = source.name,
            normalizedName = source.normalizedName,
            updated = source.updated,
            accessed = source.accessed
        )
    }
}
