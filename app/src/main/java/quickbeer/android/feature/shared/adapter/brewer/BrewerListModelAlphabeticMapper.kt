package quickbeer.android.feature.shared.adapter.brewer

import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository

class BrewerListModelAlphabeticMapper(private val brewerRepository: BrewerRepository) :
    StateMapper<List<Brewer>, List<BrewerListModel>>(
        { list ->
            list.sortedWith(compareBy(Brewer::name, Brewer::id))
                .map { BrewerListModel(it.id, brewerRepository) }
        }
    )
