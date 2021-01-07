package quickbeer.android.ui.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import quickbeer.android.util.ktx.setMarginTop

abstract class MainFragment(@LayoutRes layout: Int) : BaseFragment(layout) {

    abstract fun rootLayout(): ViewGroup
    abstract fun topInsetView(): View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout()) { _, insets ->
            topInsetView().setMarginTop(insets.systemWindowInsetTop)
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom).apply {
                ViewCompat.onApplyWindowInsets(requireView(), this)
            }
        }
    }
}
