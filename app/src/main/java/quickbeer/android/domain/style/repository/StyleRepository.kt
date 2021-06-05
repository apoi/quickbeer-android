package quickbeer.android.domain.style.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.data.state.State
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.store.StyleStore
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.network.result.ApiResult

class StyleRepository @Inject constructor(
    store: StyleStore,
    styleListRepository: StyleListRepository
) : DefaultRepository<Int, Style>(store, StyleFetcher(styleListRepository)::fetch)

/**
 * A fake fetcher using StyleListRepository for doing the fetch. This ensures the fetched style
 * list is persisted first. TODO this is a bit dirty, maybe some better approach?
 */
private class StyleFetcher(private val styleListRepository: StyleListRepository) {

    suspend fun fetch(key: Int): ApiResult<Style> {
        val state = styleListRepository.getStream(Accept())
            .filter { it !is State.Loading }
            .first()

        return when (state) {
            is State.Success -> ApiResult.Success(state.value.firstOrNull { it.id == key })
            is State.Empty -> ApiResult.Success(null)
            is State.Error -> ApiResult.UnknownError(state.cause)
            else -> error("Unexpected state $state")
        }
    }
}
