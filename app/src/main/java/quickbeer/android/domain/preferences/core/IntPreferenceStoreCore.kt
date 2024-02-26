package quickbeer.android.domain.preferences.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import quickbeer.android.data.store.core.PreferenceDataStoreCore

class IntPreferenceStoreCore(
    dataStore: DataStore<Preferences>
) : PreferenceDataStoreCore<Int>(dataStore) {

    override fun String.dataKey(): Preferences.Key<Int> {
        return intPreferencesKey(this)
    }
}
