package quickbeer.android.domain.style.store

import quickbeer.android.domain.style.Style
import quickbeer.android.util.Mapper

object StyleEntityMapper : Mapper<Style, StyleEntity> {

    override fun mapFrom(source: Style): StyleEntity {
        return StyleEntity(
            id = source.id,
            name = source.name,
            description = source.description,
            parent = source.parent,
            category = source.category
        )
    }

    override fun mapTo(source: StyleEntity): Style {
        return Style(
            id = source.id,
            name = source.name,
            description = source.description,
            parent = source.parent,
            category = source.category
        )
    }
}
