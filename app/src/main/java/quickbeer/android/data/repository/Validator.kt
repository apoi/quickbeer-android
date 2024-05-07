package quickbeer.android.data.repository

import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.repository.repository.ItemList
import quickbeer.android.util.ktx.isOlderThan

interface Validator<in V> {
    suspend fun validate(value: V?): Boolean
}

class Accept<in V> : Validator<V> {
    override suspend fun validate(value: V?): Boolean {
        return value != null
    }
}

class NoFetch<in V> : Validator<V> {
    override suspend fun validate(value: V?): Boolean {
        return true
    }
}

class AlwaysFetch<in V> : Validator<V> {

    private var validationCount = 0

    override suspend fun validate(value: V?): Boolean {
        // Reject first validation regardless of value, triggering a fetch
        return validationCount++ > 0
    }
}

class NotOlderThan<in V>(
    private val interval: Duration,
    private val updateTime: (V?) -> ZonedDateTime?
) : Validator<V> {

    override suspend fun validate(value: V?): Boolean {
        // Validate that given timestamp + update interval is still in the future
        val updated = updateTime(value)
        return updated != null && !updated.isOlderThan(interval)
    }
}

class ListCountValidator<in V>(
    private val rule: (Int) -> Boolean
) : Validator<List<V>> {
    override suspend fun validate(value: List<V>?): Boolean {
        return value != null && rule(value.size)
    }
}

class ItemListRefreshValidator<in K, in V>(
    private val rule: (ZonedDateTime) -> Boolean
) : Validator<ItemList<K, V>> {

    override suspend fun validate(value: ItemList<K, V>?): Boolean {
        return value != null && rule(value.updated)
    }
}

class ItemListRefreshAndCountValidator<in K, in V>(
    private val refreshRule: (ZonedDateTime) -> Boolean,
    private val countRule: (Int) -> Boolean
) : Validator<ItemList<K, V>> {
    override suspend fun validate(value: ItemList<K, V>?): Boolean {
        return value != null && refreshRule(value.updated) && countRule(value.values.size)
    }
}
