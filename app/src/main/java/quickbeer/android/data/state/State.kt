package quickbeer.android.data.state

/**
 * Represents data state.
 */
sealed class State<out T> {

    data class Loading<out T>(val value: T? = null) : State<T>()

    object Empty : State<Nothing>()

    data class Success<out T>(val value: T) : State<T>()

    data class Error(val cause: Throwable) : State<Nothing>()

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
