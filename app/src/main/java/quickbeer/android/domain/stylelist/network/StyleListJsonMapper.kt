package quickbeer.android.domain.stylelist.network

import quickbeer.android.domain.style.Style
import quickbeer.android.util.JsonMapper

object StyleListJsonMapper : JsonMapper<Unit, List<Style>, List<StyleJson>> {

    override fun map(key: Unit, source: List<StyleJson>): List<Style> {
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
