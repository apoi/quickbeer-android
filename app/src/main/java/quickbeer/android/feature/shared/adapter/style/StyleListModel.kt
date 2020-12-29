package quickbeer.android.feature.shared.adapter.style

import quickbeer.android.domain.style.Style
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory

class StyleListModel(val style: Style) : ListItem {

    override fun id() = style.id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)
}
