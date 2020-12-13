package quickbeer.android.ui.adapter.simple

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListAdapter<T : ListItem>(
    private val typeFactory: ListTypeFactory
) : RecyclerView.Adapter<ListViewHolder<T>>() {

    init {
        setHasStableIds(true)
    }

    private val items: MutableList<T> = mutableListOf()

    fun setItems(newItems: List<T>) {
        if (items == newItems) return

        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun itemAt(position: Int): T {
        return items[position]
    }

    fun createPool(): RecyclerView.RecycledViewPool {
        return typeFactory.createPool()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return itemAt(position).id()
    }

    override fun getItemViewType(position: Int): Int {
        return itemAt(position).type(typeFactory)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<T> {
        return typeFactory.createViewHolder(viewType, parent) as ListViewHolder<T>
    }

    override fun onBindViewHolder(holder: ListViewHolder<T>, position: Int) {
        holder.bind(itemAt(position))
    }

    override fun onViewRecycled(holder: ListViewHolder<T>) {
        holder.unbind()
    }
}
