package quickbeer.android.data.room

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import quickbeer.android.data.store.Merger
import quickbeer.android.data.store.StoreCore

/**
 * DAO base class implementing value merging and batch operations.
 */
abstract class CoreDao<K, V>(
    private val getKey: (V) -> K,
    private val merger: Merger<V>
) {

    /**
     * Splits batch get to smaller batches to fit in database limits.
     */
    protected suspend fun getBatch(keys: List<K>, get: suspend (List<K>) -> List<V>): List<V> {
        return keys.chunked(BATCH_SIZE)
            .map { get(it) }
            .flatten()
    }

    /**
     * Merges and puts a value to database. Returns changed value, or null if no change.
     */
    protected suspend fun putMerged(value: V, get: suspend (K) -> V?): V? {
        val oldValue = get(getKey(value))
        val (newValue, valuesDiffer) = StoreCore.merge(oldValue, value, merger)

        if (!valuesDiffer) {
            return null
        }

        putValue(newValue)
        return newValue
    }

    /**
     * Splits batch put to smaller batches to fit in database limits.
     */
    protected suspend fun putBatch(values: List<V>, get: suspend (List<K>) -> List<V>): List<V> {
        return values.chunked(BATCH_SIZE)
            .map { putList(it, get) }
            .flatten()
    }

    /**
     * Merges and puts a list of values to database. Returns list of changed values.
     */
    private suspend fun putList(values: List<V>, get: suspend (List<K>) -> List<V>): List<V> {
        val oldValues = get(values.map(getKey))
            .map { Pair(getKey(it), it) }
            .toMap()

        val newValues = values
            .map { StoreCore.merge(oldValues[getKey(it)], it, merger) }
            .filter { it.second } // Take only if value changed
            .map { it.first } // Merged values will be inserted

        if (newValues.isEmpty()) {
            return emptyList()
        }

        putValues(newValues)
        return newValues
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun putValue(value: V)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun putValues(values: List<V>)
}
