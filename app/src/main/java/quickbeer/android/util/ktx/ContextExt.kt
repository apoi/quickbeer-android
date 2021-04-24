package quickbeer.android.util.ktx

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat

fun Context.getThemeColor(@AttrRes colorId: Int): Int {
    val attrs = theme.obtainStyledAttributes(intArrayOf(colorId))
    val result = attrs.getColor(0, 0)
    attrs.recycle()
    return result
}

fun Context.hideKeyboard(view: View) {
    ContextCompat.getSystemService(this, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(view.windowToken, 0)
}
