package quickbeer.android.util.ktx

import android.view.ViewGroup
import android.widget.FrameLayout

fun FrameLayout.setMargins(margin: Int) {
    setMargins(margin, margin, margin, margin)
}

fun FrameLayout.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
    layoutParams = when (val params = layoutParams) {
        is ViewGroup.MarginLayoutParams -> {
            params.apply {
                setMargins(left, top, right, bottom)
            }
        }
        else -> error("Unexpected params $params")
    }
}
