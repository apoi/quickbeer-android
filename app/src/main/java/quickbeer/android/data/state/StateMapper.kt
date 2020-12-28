package quickbeer.android.data.state

open class StateMapper<in T, out R>(private val mapper: (T) -> R) {
    fun map(value: State<T>): State<R> {
        return when (value) {
            State.Loading -> State.Loading
            State.Empty -> State.Empty
            is State.Success -> State.Success(mapper(value.value))
            is State.Error -> State.Error(value.cause)
        }
    }
}

open class StateListMapper<in T, out R>(private val mapper: (T) -> R) {
    fun map(value: State<List<T>>): State<List<R>> {
        return when (value) {
            State.Loading -> State.Loading
            State.Empty -> State.Empty
            is State.Success -> State.Success(value.value.map { mapper(it) })
            is State.Error -> State.Error(value.cause)
        }
    }
}
