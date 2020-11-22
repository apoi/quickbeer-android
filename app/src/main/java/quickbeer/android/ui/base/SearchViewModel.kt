package quickbeer.android.ui.base

import android.app.Activity
import quickbeer.android.ui.adapter.search.SearchAdapter

interface SearchViewModel {

    fun getSearchAdapter(activity: Activity): SearchAdapter

    fun onSearchChanged(value: String)

    fun onSearchSubmit(value: String)

    fun onSuggestionClicked(position: Int)
}
