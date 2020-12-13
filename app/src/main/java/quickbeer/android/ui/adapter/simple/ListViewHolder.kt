package quickbeer.android.ui.adapter.simple

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ListViewHolder<in T : ListItem>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T)

    fun unbind2() {
        unbind()
    }

    open fun unbind() = Unit
}
