package quickbeer.android.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.annotation.UiThread

class ToastProvider(private val context: Context) {

    private var currentToast: Toast? = null

    @UiThread
    fun showCancelableToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        cancelIfCan()
        currentToast = Toast.makeText(context, text, length)
            .apply { show() }
    }

    @UiThread
    fun showCancelableToast(@StringRes stringId: Int, length: Int) {
        cancelIfCan()
        currentToast = Toast.makeText(context, stringId, length)
            .apply { show() }
    }

    @UiThread
    fun showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
        cancelIfCan()
        Toast.makeText(context, text, length).show()
    }

    @UiThread
    fun showToast(@StringRes stringId: Int, length: Int = Toast.LENGTH_SHORT) {
        cancelIfCan()
        Toast.makeText(context, stringId, length).show()
    }

    private fun cancelIfCan() {
        currentToast?.cancel()
        currentToast = null
    }
}
