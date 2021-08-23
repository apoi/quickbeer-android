package quickbeer.android.domain.preferences

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.inject.StringPreferenceCore

class StringPreferenceStore @Inject constructor(
    @StringPreferenceCore preferenceCore: StoreCore<String, String>
) : DefaultStore<String, String>(preferenceCore)
