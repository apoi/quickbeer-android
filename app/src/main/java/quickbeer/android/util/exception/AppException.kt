package quickbeer.android.util.exception

sealed class AppException(message: String) : Throwable(message) {
    class QueryTooShortException : AppException("Query needs to be at least four characters")
}
