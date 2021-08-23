package quickbeer.android.domain.preferences.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.core.PreferenceDataStoreCore

class StringPreferenceStoreCore(
    dataStore: DataStore<Preferences>,
    merger: Merger<String> = StoreCore.takeNew()
) : PreferenceDataStoreCore<String>(dataStore, merger) {

    override fun String.dataKey(): Preferences.Key<String> {
        return stringPreferencesKey(this)
    }
}
