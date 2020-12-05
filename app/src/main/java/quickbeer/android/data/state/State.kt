package quickbeer.android.data.state

/**
 * Represents data state.
 */
sealed class State<out T> {

    object Loading : State<Nothing>()

    object Empty : State<Nothing>()

    data class Success<out T>(val value: T) : State<T>()

    data class Error(val error: String) : State<Nothing>()
}

class StateMapper<T, R>(private val mapper: (T) -> R) {
    fun map(value: State<T>): State<R> {
        return when (value) {
            State.Loading -> State.Loading
            State.Empty -> State.Empty
            is State.Success -> State.Success(mapper(value.value))
            is State.Error -> State.Error(value.error)
        }
    }
}

class StateListMapper<T, R>(private val mapper: (T) -> R) {
    fun map(value: State<List<T>>): State<List<R>> {
        return when (value) {
            State.Loading -> State.Loading
            State.Empty -> State.Empty
            is State.Success -> State.Success(value.value.map { mapper(it) })
            is State.Error -> State.Error(value.error)
        }
    }
}
