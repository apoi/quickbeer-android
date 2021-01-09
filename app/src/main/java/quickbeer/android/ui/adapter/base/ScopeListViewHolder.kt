package quickbeer.android.ui.adapter.base

import android.view.View
import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

abstract class ScopeListViewHolder<in T : ListItem>(
    view: View
) : ListViewHolder<T>(view) {

    private var scope: CoroutineScope? = null

    final override fun bind(item: T) {
        scope = CoroutineScope(Dispatchers.IO)
            .also { bind(item, it) }
    }

    abstract fun bind(item: T, scope: CoroutineScope)

    @CallSuper
    override fun unbind() {
        scope?.cancel()
    }
}
