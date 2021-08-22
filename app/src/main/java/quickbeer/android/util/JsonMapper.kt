package quickbeer.android.util

/**
 * Interface for mapping from JSON to a domain object.
 *
 * @param <K> Type of keys.
 * @param <V> Type of domain values.
 * @param <J> Type of JSON data.
 */
interface JsonMapper<in K, out V, in J> {

    fun map(key: K, source: J): V
}
