package quickbeer.android.ui.adapter.discover

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.util.groupitem.GroupItem

data class DiscoverListModel(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    override val position: GroupItem.Position
) : ListItem, GroupItem<DiscoverListModel> {

    override fun id() = icon.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)

    override fun setPosition(position: GroupItem.Position): DiscoverListModel {
        return this.copy(position = position)
    }
}
