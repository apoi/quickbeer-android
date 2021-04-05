package quickbeer.android.ui.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Pagination trigger listener, https://gist.github.com/nesquena/d09dc68ff07e845cc622
 */
class LoadMoreListener : RecyclerView.OnScrollListener() {

    // The current offset index of data you have loaded
    private var currentPage = 0

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0

    // True if we are still waiting for the last set of data to load
    private var loading = true
    private var layoutManager: LinearLayoutManager? = null

    // Invokes with current number of items when more items should be loaded
    var moreItemRequestedCallback: ((Int) -> Unit)? = null

    fun setLayoutManager(layoutManager: LinearLayoutManager) {
        this.layoutManager = layoutManager
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val manager = layoutManager ?: return

        // Only handle down scrolls
        if (dy <= 0) {
            return
        }

        val totalItemCount = manager.itemCount
        val lastVisibleItemPosition = manager.findLastVisibleItemPosition()

        // If the total item count less than the previous count, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = STARTING_PAGE_INDEX
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the
        // current page number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visible threshold and need to reload more data. If we do need
        // to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            currentPage++
            moreItemRequestedCallback?.invoke(totalItemCount)
            loading = true
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        currentPage = STARTING_PAGE_INDEX
        previousTotalItemCount = 0
        loading = true
    }

    companion object {
        // The minimum amount of items to have below the current scroll position before loading more
        private const val VISIBLE_THRESHOLD = 5

        // Sets the starting page index
        private const val STARTING_PAGE_INDEX = 0
    }
}
