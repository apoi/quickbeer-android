package quickbeer.android.domain.preferences.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import quickbeer.android.data.store.core.PreferenceDataStoreCore

class StringPreferenceStoreCore(
    dataStore: DataStore<Preferences>
) : PreferenceDataStoreCore<String>(dataStore) {

    override fun String.dataKey(): Preferences.Key<String> {
        return stringPreferencesKey(this)
    }
}
