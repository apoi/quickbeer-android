package quickbeer.android.domain.stylelist.network

import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.network.StyleJson
import quickbeer.android.domain.style.network.StyleJsonMapper
import quickbeer.android.util.Mapper

object StyleListJsonMapper : Mapper<List<Style>, List<StyleJson>> {

    override fun mapFrom(source: List<Style>): List<StyleJson> {
        return source.map(StyleJsonMapper::mapFrom)
    }

    override fun mapTo(source: List<StyleJson>): List<Style> {
        return source.map(StyleJsonMapper::mapTo)
    }
}
