package quickbeer.android.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import quickbeer.android.util.ktx.setMarginTop

abstract class MainFragment(@LayoutRes layout: Int) : BaseFragment(layout) {

    abstract fun topInsetView(): View

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        topInsetView().setOnApplyWindowInsetsListener { _, insets ->
            topInsetView().setMarginTop(insets.systemWindowInsetTop)
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
        }
    }
}
