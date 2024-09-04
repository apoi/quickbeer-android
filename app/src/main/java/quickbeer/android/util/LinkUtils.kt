package quickbeer.android.util

object LinkUtils {

    fun fixUrl(value: String?): String? {
        return addMissingProtocol(removeTrailingSlash(value?.trim()))
    }

    private fun addMissingProtocol(value: String?): String? {
        return when {
            value == null -> null
            !value.startsWith("http") -> "http://$value"
            else -> value
        }
    }

    private fun removeTrailingSlash(value: String?): String? {
        return when {
            value == null -> null
            value.endsWith("/") -> value.dropLast(1)
            else -> value
        }
    }
}
