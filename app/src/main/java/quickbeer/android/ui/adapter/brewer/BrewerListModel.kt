package quickbeer.android.ui.adapter.brewer

import kotlinx.coroutines.flow.firstOrNull
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.ui.adapter.simple.ListItem
import quickbeer.android.ui.adapter.simple.ListTypeFactory

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

    suspend fun getBrewer(brewerId: Int): Brewer? {
        return brewerRepository.getStream(brewerId, Brewer.BasicDataValidator())
            .firstOrNull { it is State.Success }
            ?.let { if (it is State.Success) it.value else null }
    }

    suspend fun getCountry(countryId: Int?): Country? {
        if (countryId == null) return null

        return countryRepository.getStream(countryId, Accept())
            .firstOrNull { it is State.Success }
            ?.let { if (it is State.Success) it.value else null }
    }
}
