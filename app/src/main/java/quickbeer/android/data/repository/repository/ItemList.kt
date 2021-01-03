package quickbeer.android.data.repository.repository

import org.threeten.bp.ZonedDateTime

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
)
