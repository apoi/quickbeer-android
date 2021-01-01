package quickbeer.android.ui.adapter.simple

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ListAdapter<T : ListItem>(
    private val typeFactory: ListTypeFactory,
    private val lazyListItems: Boolean = true
) : RecyclerView.Adapter<ListViewHolder<T>>() {

    init {
        setHasStableIds(true)
    }

    private val items: MutableList<T> = mutableListOf()

    fun setItems(newItems: List<T>): Boolean {
        if (items == newItems) return false

        val diffCallback = DiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
        return true
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

    inner class DiffCallback(
        private val old: List<T>,
        private val new: List<T>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return old.size
        }

        override fun getNewListSize(): Int {
            return new.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return old[oldItemPosition].id() == new[newItemPosition].id()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Lazy items update themselves and do not require explicit list updates
            return if (lazyListItems) areItemsTheSame(oldItemPosition, newItemPosition)
            else return old[oldItemPosition] == new[newItemPosition]
        }
    }
}
