package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.NetworkUtils;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.pojo.ReviewList;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ReviewFetcher extends FetcherBase {
    private static final String TAG = ReviewFetcher.class.getSimpleName();

    protected final NetworkApi networkApi;
    private final ReviewStore reviewStore;
    private final ReviewListStore reviewListStore;

    public ReviewFetcher(@NonNull NetworkApi networkApi,
                         @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                         @NonNull ReviewStore reviewStore,
                         @NonNull ReviewListStore reviewListStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network api cannot be null.");
        Preconditions.checkNotNull(reviewStore, "Review store cannot be null.");
        Preconditions.checkNotNull(reviewListStore, "Review list store cannot be null.");

        this.networkApi = networkApi;
        this.reviewStore = reviewStore;
        this.reviewListStore = reviewListStore;
    }

    @Override
    public void fetch(@NonNull Intent intent) {
        final int beerId = intent.getIntExtra("beerId", -1);

        if (beerId > 0) {
            fetchReviews(beerId);
        } else {
            Log.e(TAG, "No beerId provided in the intent extras");
        }
    }

    protected void fetchReviews(final int beerId) {
        Log.d(TAG, "fetchReviews(" + beerId + ")");

        if (requestMap.containsKey(beerId) &&
                !requestMap.get(beerId).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for reviews " + beerId);
            return;
        }

        final String uri = reviewListStore.getUriForId(beerId).toString();
        Subscription subscription = createNetworkObservable(beerId)
                .subscribeOn(Schedulers.computation())
                .map((reviews) -> {
                    final List<Integer> reviewIds = new ArrayList<>();
                    for (Review review : reviews) {
                        reviewStore.put(review);
                        reviewIds.add(review.getId());
                    }
                    return new ReviewList(beerId, reviewIds);
                })
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe(reviewListStore::put,
                        e -> Log.e(TAG, "Error fetching reviews for '" + beerId + "'", e));

        requestMap.put(beerId, subscription);
        startRequest(uri);
    }

    @NonNull
    protected Observable<List<Review>> createNetworkObservable(final int beerId) {
        return networkApi.getReviews(NetworkUtils.createRequestParams("bid", String.valueOf(beerId)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.REVIEWS;
    }
}
