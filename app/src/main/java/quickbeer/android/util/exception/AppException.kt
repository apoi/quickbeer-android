package quickbeer.android.util.exception

sealed class AppException(override val message: String) : Throwable(message) {
    class NoSearchEntered : AppException("")
    class QueryTooShortException : AppException("Query needs to be at least four characters")
}
