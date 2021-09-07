package quickbeer.android.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import quickbeer.android.util.ktx.setMarginTop

abstract class MainFragment(@LayoutRes layout: Int) : BaseFragment(layout) {

    abstract fun topInsetView(): View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        topInsetView().setOnApplyWindowInsetsListener { _, insets ->
            topInsetView().setMarginTop(insets.systemWindowInsetTop)
            insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
        }
    }

    override fun onResume() {
        super.onResume()

        // Navigate to pending targets, such as barcode scanning result
        requireMainActivity()
            .getPendingDestination()
            ?.let(::navigate)
    }
}
