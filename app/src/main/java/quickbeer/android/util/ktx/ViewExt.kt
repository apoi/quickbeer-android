package quickbeer.android.util.ktx

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import androidx.core.content.ContextCompat.getSystemService

fun View.showKeyboard() {
    getSystemService(context, InputMethodManager::class.java)
        ?.toggleSoftInputFromWindow(applicationWindowToken, SHOW_FORCED, 0)
}
