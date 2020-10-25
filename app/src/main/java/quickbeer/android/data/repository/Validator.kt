package quickbeer.android.data.repository

interface Validator<in V> {
    fun validate(value: V): Boolean
}

class Accept<in V> : Validator<V> {
    override fun validate(value: V) = true
}
