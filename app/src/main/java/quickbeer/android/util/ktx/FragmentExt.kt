package quickbeer.android.util.ktx

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import quickbeer.android.data.state.State

/**
 * LiveData observing helper.
 */
fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (T) -> Unit) {
    liveData.observe(this, observer::invoke)
}

fun <T> LifecycleOwner.observeSuccess(liveData: LiveData<State<T>>, observer: (T) -> Unit) {
    liveData.observe(this, { if (it is State.Success) observer(it.value) })
}

fun Fragment.hideKeyboard() {
    requireContext().hideKeyboard(requireView())
}

/**
 * Keep ViewBinding in View tag for convenient lifecycle management.
 */
inline fun <reified T : ViewBinding> Fragment.viewBinding(
    crossinline bind: (View) -> T
): ReadOnlyProperty<Fragment, T> = object : ReadOnlyProperty<Fragment, T> {

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val value = requireView().getTag(property.name.hashCode()) as? T
        if (value != null) return value

        return bind(requireView()).also {
            requireView().setTag(property.name.hashCode(), it)
        }
    }
}
