package quickbeer.android.ui.search

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.R
import androidx.appcompat.widget.SearchView

class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.searchViewStyle
) : SearchView(context, attrs, defStyle) {

    private var oldQuery: CharSequence? = null

    /**
     * Expanding clears query by default, workaround for that
     */
    override fun onActionViewExpanded() {
        super.onActionViewExpanded()
        setQuery(oldQuery, false)
    }

    /**
     * Save old query when search is collapsed
     */
    override fun onActionViewCollapsed() {
        oldQuery = query
        super.onActionViewCollapsed()
    }

    /**
     * Override saved query if item is selected
     */
    fun onQuerySelected(query: CharSequence) {
        oldQuery = query
    }
}
