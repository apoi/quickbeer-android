package quickbeer.android.domain.preferences

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.inject.IntPreferenceCore

class IntPreferenceStore @Inject constructor(
    @IntPreferenceCore preferenceCore: StoreCore<String, Int>
) : DefaultStore<String, Int>(preferenceCore)
