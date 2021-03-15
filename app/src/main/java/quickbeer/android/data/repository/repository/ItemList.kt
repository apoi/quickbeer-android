package quickbeer.android.data.repository.repository

import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.store.Merger

/**
 * List of items for storing lists in ItemListStore.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
data class ItemList<out K, out V>(
    val key: K,
    val values: List<V>,
    val updated: ZonedDateTime
) {

    companion object {

        /**
         * Merger that combines the both lists.
         */
        fun <K, V> join(): Merger<ItemList<K, V>> {
            return { old, new ->
                ItemList(old.key, (old.values + new.values).distinct(), new.updated)
            }
        }
    }
}
