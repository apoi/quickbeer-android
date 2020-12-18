package quickbeer.android.domain.style.network

import quickbeer.android.domain.style.Style
import quickbeer.android.util.Mapper

object StyleJsonMapper : Mapper<Style, StyleJson> {

    override fun mapFrom(source: Style): StyleJson {
        return StyleJson(
            id = source.id,
            name = source.name,
            description = source.description,
            parent = source.parent,
            category = source.category
        )
    }

    override fun mapTo(source: StyleJson): Style {
        return Style(
            id = source.id,
            name = source.name,
            description = source.description,
            parent = source.parent,
            category = source.category
        )
    }
}
