package quickbeer.android.domain.preferences.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.preferences.core.IntPreferenceStoreCore

class IntPreferenceStore @Inject constructor(
    preferenceCore: IntPreferenceStoreCore
) : DefaultStore<String, Int>(preferenceCore)
