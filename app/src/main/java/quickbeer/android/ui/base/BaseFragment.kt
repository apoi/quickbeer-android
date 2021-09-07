package quickbeer.android.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import quickbeer.android.feature.MainActivity
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavAnim

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    private var isInitialView = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isInitialView) {
            onInitialViewCreated()
        } else {
            onRestoreView()
        }

        observeViewState()
    }

    open fun onInitialViewCreated() = Unit

    open fun onRestoreView() = Unit

    override fun onDestroyView() {
        isInitialView = false
        super.onDestroyView()
    }

    open fun observeViewState() = Unit

    protected fun requireMainActivity(): MainActivity {
        return requireActivity() as MainActivity
    }

    protected fun navigate(navDirections: NavDirections, anim: NavAnim = NavAnim.DEFAULT) {
        findNavController()
            .navigate(navDirections, anim.navOptions())
    }

    protected fun navigate(destination: Destination, anim: NavAnim = NavAnim.DEFAULT) {
        findNavController()
            .navigate(destination.uri, anim.navOptions())
    }
}
