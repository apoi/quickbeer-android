package quickbeer.android.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import androidx.core.view.children

class LinearInsetLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        children.forEach { child ->
            child.dispatchApplyWindowInsets(WindowInsets(insets))
        }

        return insets
    }
}
