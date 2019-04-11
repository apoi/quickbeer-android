package quickbeer.android.listeners;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Pagination trigger listener, https://gist.github.com/nesquena/d09dc68ff07e845cc622
 */
public class LoadMoreListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position before loading more
    private static final int VISIBLE_THRESHOLD = 5;

    // Sets the starting page index
    private static final int STARTING_PAGE_INDEX = 0;

    // The current offset index of data you have loaded
    private int currentPage = 0;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;

    // True if we are still waiting for the last set of data to load
    private boolean loading = true;

    private RecyclerView.LayoutManager layoutManager;

    @NonNull
    private final PublishSubject<Integer> loadMoreSubject = PublishSubject.create();

    public void setLayoutManager(@NonNull LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        // Only handle down scrolls
        if (dy <= 0) {
            return;
        }

        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            currentPage = STARTING_PAGE_INDEX;
            previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                loading = true;
            }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the
        // current page number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visible threshold and need to reload more data. If we do need
        // to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && (lastVisibleItemPosition + VISIBLE_THRESHOLD) >= totalItemCount) {
            currentPage++;
            loadMoreSubject.onNext(totalItemCount);
            //onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    // Call this method whenever performing new searches
    public void resetState() {
        currentPage = STARTING_PAGE_INDEX;
        previousTotalItemCount = 0;
        loading = true;
    }

    /**
     * Emits an integer with current number of items when more items should be loaded.
     */
    @NonNull
    public Observable<Integer> moreItemsRequestedStream() {
        return loadMoreSubject;
    }
}