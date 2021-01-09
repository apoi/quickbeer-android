package quickbeer.android.ui.adapter.country

import quickbeer.android.domain.country.Country
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class CountryListModel(val country: Country) : ListItem {

    override fun id() = country.id.toLong()

    override fun type(factory: ListTypeFactory) = factory.type(this)
}
