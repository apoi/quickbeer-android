package quickbeer.android.util.ktx

import android.content.Context
import androidx.annotation.AttrRes

fun Context.getThemeColor(@AttrRes colorId: Int): Int {
    val attrs = theme.obtainStyledAttributes(intArrayOf(colorId))
    val result = attrs.getColor(0, 0)
    attrs.recycle()
    return result
}
