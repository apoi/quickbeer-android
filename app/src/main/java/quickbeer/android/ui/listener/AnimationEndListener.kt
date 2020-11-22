package quickbeer.android.ui.listener

import android.animation.Animator
import android.animation.AnimatorListenerAdapter

class AnimationEndListener(private val callback: () -> Unit) : AnimatorListenerAdapter() {

    override fun onAnimationEnd(animation: Animator) {
        callback.invoke()
    }
}
