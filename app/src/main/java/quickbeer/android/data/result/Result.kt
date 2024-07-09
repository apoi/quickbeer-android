package quickbeer.android.data.result

sealed class Result<out T> {

    data class Success<T>(val value: T) : Result<T>()

    data class Failure(val cause: Throwable) : Result<Nothing>()
}
