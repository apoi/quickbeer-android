package quickbeer.android.util.ktx

import android.content.res.Resources
import kotlin.math.roundToInt

fun Resources.dpToPx(dp: Int): Int {
    return (displayMetrics.density * dp).roundToInt()
}