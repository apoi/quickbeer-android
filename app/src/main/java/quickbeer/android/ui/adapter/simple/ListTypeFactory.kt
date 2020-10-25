package quickbeer.android.ui.adapter.simple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ListTypeFactory {

    abstract fun type(item: ListItem): Int

    abstract fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*>

    protected fun <T : ViewBinding> createBinding(
        creator: (inflater: LayoutInflater, root: ViewGroup, attachToRoot: Boolean) -> T,
        parent: ViewGroup
    ): T {
        return creator(LayoutInflater.from(parent.context), parent, false)
    }
}
