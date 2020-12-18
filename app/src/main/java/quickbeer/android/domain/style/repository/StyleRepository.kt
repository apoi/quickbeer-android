package quickbeer.android.domain.style.repository

import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.store.StyleStore

class StyleRepository(
    store: StyleStore,
) : DefaultRepository<Int, Style>(
    store, { error("Not implemented") }
)
