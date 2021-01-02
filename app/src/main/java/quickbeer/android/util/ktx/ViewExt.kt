package quickbeer.android.util.ktx

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_FORCED
import androidx.core.content.ContextCompat.getSystemService

fun View.showKeyboard() {
    getSystemService(context, InputMethodManager::class.java)
        ?.toggleSoftInputFromWindow(applicationWindowToken, SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    getSystemService(context, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(windowToken, 0)
}

fun View.onGlobalLayout(block: () -> Unit) {
    if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                block()
            }
        })
    }
}

fun View.setMargins(margin: Int) {
    setMargins(margin, margin, margin, margin)
}

fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    layoutParams = when (val params = layoutParams) {
        is ViewGroup.MarginLayoutParams -> {
            params.apply {
                setMargins(left, top, right, bottom)
            }
        }
        else -> error("Unexpected params")
    }
}

fun View.setMarginTop(marginTop: Int) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(0, marginTop, 0, 0)
    layoutParams = params
}
