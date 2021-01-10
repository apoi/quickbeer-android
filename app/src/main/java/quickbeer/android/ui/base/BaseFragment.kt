package quickbeer.android.ui.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import quickbeer.android.R
import quickbeer.android.navigation.Destination

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
        super.onDestroyView()
        isInitialView = false
    }

    open fun observeViewState() = Unit

    protected fun navigate(navDirections: NavDirections, anim: NavAnim = NavAnim.DEFAULT) {
        findNavController()
            .navigate(navDirections, navOptions(anim))
    }

    protected fun navigate(destination: Destination, anim: NavAnim = NavAnim.DEFAULT) {
        findNavController()
            .navigate(destination.uri, navOptions(anim))
    }

    private fun navOptions(anim: NavAnim): NavOptions? {
        if (anim == NavAnim.NONE) {
            return NavOptions.Builder()
                .setExitAnim(R.anim.hold)
                .setPopEnterAnim(R.anim.hold)
                .build()
        }

        return NavOptions.Builder()
            .setEnterAnim(R.anim.enter_anim)
            .setExitAnim(R.anim.exit_anim)
            .setPopEnterAnim(R.anim.pop_enter_anim)
            .setPopExitAnim(R.anim.pop_exit_anim)
            .build()
    }

    enum class NavAnim {
        DEFAULT,
        NONE
    }
}
