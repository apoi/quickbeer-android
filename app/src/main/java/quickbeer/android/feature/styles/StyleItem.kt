package quickbeer.android.feature.styles

import quickbeer.android.domain.style.Style
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory

class StyleItem(val style: Style) : ListItem {

    override fun id() = style.id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)
}
