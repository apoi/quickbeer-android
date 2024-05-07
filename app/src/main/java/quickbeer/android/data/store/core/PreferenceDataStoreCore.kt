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
 * StoreCore with Preferences DataStore as the backing store.
 *
 * @param <V> Type of values.
 */
abstract class PreferenceDataStoreCore<V : Any>(
    private val dataStore: DataStore<Preferences>
) : StoreCore<String, V> {

    // Type specific getter for keys
    protected abstract fun String.dataKey(): Preferences.Key<V>

    // Flow of all updated values
    private val putStream = MutableSharedFlow<V>()

    // Flow of all deleted keys
    private val deleteStream = MutableSharedFlow<String>()

    // No value merging needed for primitive values, just take the new one
    private val merger: Merger<V> = StoreCore.takeNew()

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

    override suspend fun getAll(): List<V> {
        // Preferences DataStore can't list all values of a given type. This shouldn't be an
        // issue as listing all values of a primitive type key-value store isn't useful anyway.
        error("This core does not support enumeration of all values")
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

    override suspend fun deleteAll(): Boolean {
        // Preferences DataStore can't list all keys of a given type, so we can't emit delete with
        // support of deleteStream. Shouldn't be an issue for the use cases.
        error("This core does not support deletion of all values")
    }

    override fun getDeleteStream(): Flow<String> {
        return deleteStream
    }
}
