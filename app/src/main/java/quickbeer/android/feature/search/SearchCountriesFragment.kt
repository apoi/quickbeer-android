package quickbeer.android.feature.search

import quickbeer.android.navigation.Destination
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.adapter.country.CountryListModel
import quickbeer.android.ui.adapter.country.CountryTypeFactory
import quickbeer.android.ui.recyclerview.RecycledPoolHolder.PoolType

class SearchCountriesFragment : SearchTabFragment<CountryListModel>() {

    override val resultAdapter = ListAdapter<CountryListModel>(CountryTypeFactory())
    override val resultPoolType = PoolType.COUNTRY_LIST
    override fun resultFlow() = viewModel.countryResults

    override fun onItemSelected(item: CountryListModel) {
        navigate(Destination.Country(item.country.id))
    }
}
