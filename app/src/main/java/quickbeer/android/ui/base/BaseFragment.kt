package quickbeer.android.ui.base

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import quickbeer.android.R
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


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val activity = requireActivity()

        // Navigating between tabs should not use the default navigation animation
        if (activity is MainActivity && activity.suppressNavAnimation) {
            return if (enter) {
                // Animation order is exit first, then enter. Reset the flag after enter.
                activity.suppressNavAnimation = false
                AnimationUtils.loadAnimation(requireContext(), R.anim.no_anim)
            } else {
                AnimationUtils.loadAnimation(requireContext(), R.anim.no_anim)
            }
        }

        // Default behaviour is to use the default animation
        return super.onCreateAnimation(transit, enter, nextAnim)
    }
}
