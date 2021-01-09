package quickbeer.android.ui.adapter.style

import quickbeer.android.domain.style.Style
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class StyleListModel(val style: Style) : ListItem {

    override fun id() = style.id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)
}
