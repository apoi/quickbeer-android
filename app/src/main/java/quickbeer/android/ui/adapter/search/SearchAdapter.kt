package quickbeer.android.ui.adapter.search

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import quickbeer.android.R

open class SearchAdapter(activity: Activity) :
    CursorAdapter(activity, null, FLAG_REGISTER_CONTENT_OBSERVER) {

    private val items = mutableListOf<SearchResult>()

    fun setItems(newItems: List<SearchResult>) {
        if (items == newItems) return

        val cursor = MatrixCursor(arrayOf(BaseColumns._ID))
        newItems.forEachIndexed { index, _ ->
            cursor.addRow(arrayOf<Any>(index))
        }

        items.clear()
        items.addAll(newItems)

        changeCursor(cursor)
        notifyDataSetChanged()
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        (view.tag as SuggestionViewHolder).bind(items[cursor.position])
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val inflater: LayoutInflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return inflater.inflate(R.layout.search_view_suggestion, parent, false)
            .apply { tag = SuggestionViewHolder(this) }
    }
}
