package quickbeer.android.util.ktx

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle.State.INITIALIZED
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Convenience method for creating ViewBinding from Fragment's view with View
 * lifecycle using `viewLifecycle` extension below.
 */
fun <T : ViewBinding> Fragment.viewBinding(bind: (View) -> T) =
    viewLifecycle { bind(requireView()) }

/**
 * Binds a value to View lifecycle. Value is created with the initialization
 * function at View's onCreate, and destroyed at onDestroy.
 */
fun <T> Fragment.viewLifecycle(create: () -> T): ReadOnlyProperty<Fragment, T> =

    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

        private var value: T? = null

        init {
            // Observe the View lifecycle of the Fragment
            this@viewLifecycle
                .viewLifecycleOwnerLiveData
                .observe(
                    this@viewLifecycle,
                    Observer { owner ->
                        viewLifecycleOwner.lifecycle.removeObserver(this)
                        owner.lifecycle.addObserver(this)
                    }
                )
        }

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            value = create()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            value = null
        }

        override fun getValue(
            thisRef: Fragment,
            property: KProperty<*>
        ): T {
            value?.let { return@getValue it }

            if (!lifecycle.currentState.isAtLeast(INITIALIZED)) {
                error("View has already been destroyed")
            }

            return create().also {
                value = it
            }
        }
    }
