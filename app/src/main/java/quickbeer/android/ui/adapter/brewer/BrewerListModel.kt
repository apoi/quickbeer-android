package quickbeer.android.ui.adapter.brewer

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class BrewerListModel(
    val brewerId: Int,
    val countryId: Int?,
    private val brewerRepository: BrewerRepository,
    private val countryRepository: CountryRepository
) : ListItem {

    override fun id(): Long {
        return brewerId.toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }

    fun getBrewer(brewerId: Int): Flow<State<Brewer>> {
        return brewerRepository.getStream(brewerId, Brewer.BasicDataValidator())
    }

    fun getCountry(countryId: Int): Flow<State<Country>> {
        return countryRepository.getStream(countryId, Accept())
    }
}
