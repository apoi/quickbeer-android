package quickbeer.android.data.store.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore

/**
 * StoreCore with Preferences DataStore as the backing store. For type safety reasons the store
 * must be exclusive for each Core -- we can't enumerate all values of a single type from a mixed
 * store.
 *
 * @param <V> Type of values.
 */
abstract class PreferenceDataStoreCore<V>(
    private val dataStore: DataStore<Preferences>,
    private val merger: Merger<V> = StoreCore.takeNew()
) : StoreCore<String, V> {

    // Type specific getter for keys
    protected abstract fun String.dataKey(): Preferences.Key<V>

    // Flow of all updated values
    private val putStream = MutableSharedFlow<V>()

    // Flow of all deleted keys
    private val deleteStream = MutableSharedFlow<String>()

    override suspend fun get(key: String): V? {
        return dataStore.data.first()[key.dataKey()]
    }

    override suspend fun get(keys: List<String>): List<V> {
        return dataStore.data.first()
            .let { preferences -> keys.mapNotNull { preferences[it.dataKey()] } }
    }

    override fun getStream(key: String): Flow<V> {
        return dataStore.data.mapNotNull { it[key.dataKey()] }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun getAll(): List<V> {
        return getAllStream().first()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAllStream(): Flow<List<V>> {
        return dataStore.data.map { preferences ->
            preferences.asMap().values.toList().map { it as V }
        }
    }

    override suspend fun getKeys(): List<String> {
        return getKeysStream().first()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getKeysStream(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            preferences.asMap().keys.toList().map { it.name }
        }
    }

    @Suppress("ReplaceGetOrSet")
    override suspend fun put(key: String, value: V): V? {
        var valuesDiffer = false

        return dataStore
            .edit { preferences ->
                val oldValue = preferences[key.dataKey()]
                val mergeResult = mergeValues(oldValue, value, merger)
                val newValue = mergeResult.first
                valuesDiffer = mergeResult.second

                if (valuesDiffer) {
                    preferences[key.dataKey()] = newValue
                    putStream.emit(newValue)
                }
            }
            .get(key.dataKey())
            .takeIf { valuesDiffer }
    }

    override suspend fun put(items: Map<String, V>): List<V> {
        return dataStore
            .edit { preferences ->
                items.forEach { (key, value) ->
                    val oldValue = preferences[key.dataKey()]
                    val (newValue, valuesDiffer) = mergeValues(oldValue, value, merger)

                    if (valuesDiffer) {
                        preferences[key.dataKey()] = newValue
                        putStream.emit(newValue)
                    }
                }
            }
            .let { preferences ->
                items.keys.mapNotNull { preferences[it.dataKey()] }
            }
    }

    override fun getPutStream(): Flow<V> {
        return putStream
    }

    override suspend fun delete(key: String): Boolean {
        var valueDeleted = false

        return dataStore
            .edit { preferences ->
                if (preferences.contains(key.dataKey())) {
                    preferences.remove(key.dataKey())
                    deleteStream.emit(key)
                    valueDeleted = true
                }
            }
            .let { valueDeleted }
    }

    override fun getDeleteStream(): Flow<String> {
        return deleteStream
    }
}
