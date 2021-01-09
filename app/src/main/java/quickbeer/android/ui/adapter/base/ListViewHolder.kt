package quickbeer.android.ui.adapter.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ListViewHolder<in T : ListItem>(
    view: View
) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: T)

    open fun unbind() = Unit
}
