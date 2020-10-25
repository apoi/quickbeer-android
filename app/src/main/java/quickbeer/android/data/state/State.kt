package quickbeer.android.data.state

/**
 * Represents data state.
 */
sealed class State<out T> {

    object Loading : State<Nothing>()

    data class Success<out T>(val value: T) : State<T>()

    object Empty : State<Nothing>()

    data class Error(val error: String) : State<Nothing>()
}
