package quickbeer.android.util.ktx

import quickbeer.android.R
import quickbeer.android.util.exception.AppException

fun Throwable.getMessage(getString: (Int) -> String): String {
    return when (this) {
        is AppException -> message
        else -> getString(R.string.message_error)
    }
}
