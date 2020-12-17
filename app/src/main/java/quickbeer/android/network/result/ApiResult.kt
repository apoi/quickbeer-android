package quickbeer.android.network.result

import quickbeer.android.network.result.ApiResult.HttpError
import quickbeer.android.network.result.ApiResult.NetworkError
import quickbeer.android.network.result.ApiResult.Success
import quickbeer.android.network.result.ApiResult.UnknownError
import quickbeer.android.util.Mapper

/**
 * Wrapper class for network request results.
 */
sealed class ApiResult<out T> {

    data class Success<out T>(val value: T?) : ApiResult<T>()

    data class HttpError(val code: Int, val cause: Throwable) : ApiResult<Nothing>()

    data class NetworkError(val cause: Throwable) : ApiResult<Nothing>()

    data class UnknownError(val cause: Throwable) : ApiResult<Nothing>()
}

/**
 * Result mapper for API calls where a single item is returned as list.
 */
fun <T, U : List<T>> ApiResult<U>.first(): ApiResult<T> {
    return when (this) {
        is Success -> Success(value?.first())
        is HttpError -> HttpError(code, cause)
        is NetworkError -> NetworkError(cause)
        is UnknownError -> UnknownError(cause)
    }
}

/**
 * Result mapper from one type to another.
 */
fun <T, U> ApiResult<U>.map(mapper: Mapper<T, U>): ApiResult<T> {
    return when (this) {
        is Success -> Success(value?.let(mapper::mapTo))
        is HttpError -> HttpError(code, cause)
        is NetworkError -> NetworkError(cause)
        is UnknownError -> UnknownError(cause)
    }
}
