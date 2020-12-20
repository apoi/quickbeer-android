package quickbeer.android.domain.stylelist.network

import quickbeer.android.domain.style.Style
import quickbeer.android.util.Mapper

object StyleListJsonMapper : Mapper<List<Style>, List<StyleJson>> {

    override fun mapFrom(source: List<Style>): List<StyleJson> {
        return source.map {
            StyleJson(
                id = it.id,
                name = it.name,
                description = it.description,
                parent = it.parent,
                category = it.category
            )
        }
    }

    override fun mapTo(source: List<StyleJson>): List<Style> {
        return source.map {
            Style(
                id = it.id,
                name = it.name,
                description = it.description,
                parent = it.parent,
                category = it.category
            )
        }
    }
}
