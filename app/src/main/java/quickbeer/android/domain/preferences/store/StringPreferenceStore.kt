package quickbeer.android.domain.preferences.store

import javax.inject.Inject
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.preferences.core.StringPreferenceStoreCore

class StringPreferenceStore @Inject constructor(
    preferenceCore: StringPreferenceStoreCore
) : DefaultStore<String, String>(preferenceCore)
