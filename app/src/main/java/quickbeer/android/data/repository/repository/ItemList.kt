package quickbeer.android.data.repository.repository

/**
 * List of items for storing lists in ItemListStore.
 *
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
open class ItemList<out K, out V>(
    val key: K,
    val values: List<V>
)
