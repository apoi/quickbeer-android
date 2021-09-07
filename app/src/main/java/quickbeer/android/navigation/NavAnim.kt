package quickbeer.android.navigation

import androidx.navigation.NavOptions
import quickbeer.android.R

enum class NavAnim {
    DEFAULT,
    NONE;

    fun navOptions(): NavOptions {
        if (this == NavAnim.NONE) {
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
}
