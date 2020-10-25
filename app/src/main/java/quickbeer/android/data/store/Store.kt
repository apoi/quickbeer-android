package quickbeer.android.data.store

import kotlinx.coroutines.flow.Flow

/**
 * Default interface for a store. A store acts as a data container, in which all data items are
 * identified with a key that can be deduced from the value itself. Usually this would be done
 * through a function such as `getKey(value: V): K`, but it can also be defined in the store
 * implementation itself.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
interface Store<in K, V> {

    /**
     * Returns value for given key, or empty value if key doesn't exist in store.
     */
    suspend fun get(key: K): V?

    /**
     * Returns stream of future values for given key, starting with the current
     * value if one exists.
     */
    fun getStream(key: K): Flow<V>

    /**
     * Puts the value to the store.
     *
     * @return True if operation was successful, otherwise false.
     */
    suspend fun put(value: V): Boolean

    /**
     * Deletes value matching the key from the store.
     *
     * @return True if operation was successful, otherwise false.
     */
    suspend fun delete(key: K): Boolean
}

/**
 * Special case of Store that can only contain a single value.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
interface SingleStore<V> {

    suspend fun get(): V

    fun getStream(): Flow<V>

    suspend fun put(value: V): Boolean

    suspend fun delete(): Boolean
}
