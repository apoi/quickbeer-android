package quickbeer.android.ui.listener

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup

class LayoutTransitionEndListener(
    private val callback: () -> Unit
) : LayoutTransition.TransitionListener {

    override fun startTransition(
        transition: LayoutTransition?,
        container: ViewGroup?,
        view: View?,
        transitionType: Int
    ) = Unit

    override fun endTransition(
        transition: LayoutTransition?,
        container: ViewGroup?,
        view: View?,
        transitionType: Int
    ) {
        transition?.removeTransitionListener(this)
        callback.invoke()
    }
}
