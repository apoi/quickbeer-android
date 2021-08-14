package quickbeer.android.data.repository

interface Validator<in V> {
    suspend fun validate(value: V?): Boolean
}

class Accept<in V> : Validator<V> {
    override suspend fun validate(value: V?): Boolean {
        return value != null
    }
}

class Reject<in V> : Validator<V> {
    override suspend fun validate(value: V?): Boolean {
        return false
    }
}

class NoFetch<in V> : Validator<V> {
    override suspend fun validate(value: V?): Boolean {
        return true
    }
}
