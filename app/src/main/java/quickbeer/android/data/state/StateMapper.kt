package quickbeer.android.data.state

open class StateMapper<in T, out R>(private val mapper: (T) -> R) {
    fun map(value: State<T>) = value.map(mapper)
}
