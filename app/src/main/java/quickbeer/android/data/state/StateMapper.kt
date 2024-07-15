package quickbeer.android.data.state

open class StateMapper<in T, out R>(private val mapper: (T) -> R) {
    fun map(value: State<T>): State<R> {
        return when (value) {
            is State.Initial -> State.Initial
            is State.Loading -> State.Loading(value.value?.let(mapper))
            is State.Empty -> State.Empty
            is State.Success -> State.Success(mapper(value.value))
            is State.Error -> State.Error(value.cause)
        }
    }
}
