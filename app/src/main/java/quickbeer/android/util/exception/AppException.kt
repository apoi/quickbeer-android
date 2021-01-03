package quickbeer.android.util.exception

sealed class AppException(override val message: String) : Throwable(message) {
    object NoSearchEntered : AppException("")
    object QueryTooShortException : AppException("Query needs to be at least four characters")
    object RepositoryFilterFailed : AppException("Filter doesn't pass")
}
