package quickbeer.android.data.state

/**
 * Represents data state.
 */
sealed class State<out T> {

    data object Initial : State<Nothing>()

    data class Loading<out T>(val value: T? = null) : State<T>()

    data object Empty : State<Nothing>()

    data class Success<out T>(val value: T) : State<T>()

    data class Error(val cause: Throwable) : State<Nothing>()

    fun valueOrNull(): T? {
        return when (this) {
            is Initial -> null
            is Loading -> value
            is Empty -> null
            is Success -> value
            is Error -> null
        }
    }

    companion object {
        fun <T> from(value: T?): State<T> {
            return when {
                value == null -> Empty
                value is List<*> && value.isEmpty() -> Empty
                else -> Success(value)
            }
        }
    }
}
