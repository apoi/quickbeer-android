package quickbeer.android.util.ktx

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State

/**
 * Flow observing helper.
 */
fun <T> Fragment.observe(flow: Flow<T>, observer: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest { event -> observer(event) }
        }
    }
}

fun <T> Fragment.observeSuccess(flow: Flow<State<T>>, observer: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest { if (it is State.Success) observer(it.value) }
        }
    }
}

fun Fragment.hideKeyboard() {
    requireContext().hideKeyboard(requireView())
}

/**
 * Keep ViewBinding in View tag for convenient lifecycle management.
 */
inline fun <reified T : ViewBinding> Fragment.viewBinding(
    crossinline bind: (View) -> T,
    noinline destroyCallback: ((T) -> Unit)? = null
): ReadOnlyProperty<Fragment, T> = object : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null

    init {
        viewLifecycleOwnerLiveData.observe(this@viewBinding) { viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding?.let { destroyCallback?.invoke(it) }
                    binding = null
                }
            })
        }
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding?.let { return it }

        val viewLifecycleOwner = try {
            thisRef.viewLifecycleOwner
        } catch (e: IllegalStateException) {
            error("Fragment view hasn't been created")
        }

        if (!viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Attempt to access binding after Fragment view was destroyed")
        }

        return bind(thisRef.requireView()).also { viewBinding ->
            binding = viewBinding
        }
    }
}
