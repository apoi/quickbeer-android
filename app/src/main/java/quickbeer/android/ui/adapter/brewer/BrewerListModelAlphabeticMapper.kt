package quickbeer.android.ui.adapter.brewer

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.repository.CountryRepository

class BrewerListModelAlphabeticMapper(
    private val brewerRepository: BrewerRepository,
    private val countryRepository: CountryRepository
) : StateMapper<List<Brewer>, List<BrewerListModel>>(
    { list ->
        list.sortedWith(compareBy(Brewer::name, Brewer::id))
            .map { BrewerListModel(it.id, it.countryId, brewerRepository, countryRepository) }
    }
)
