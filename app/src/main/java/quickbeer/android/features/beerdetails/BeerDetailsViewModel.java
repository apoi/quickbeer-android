/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.features.beerdetails;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.Constants;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.providers.GlobalNotificationProvider;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.viewmodels.BrewerViewModel;
import quickbeer.android.viewmodels.ReviewListViewModel;
import rx.Observable;
import rx.functions.Actions;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsViewModel extends SimpleViewModel {

    @NonNull
    private final BeerViewModel beerViewModel;

    @NonNull
    private final BrewerViewModel brewerViewModel;

    @NonNull
    private final ReviewListViewModel reviewListViewModel;

    @NonNull
    private final PublishSubject<Boolean> tickSuccessSubject = PublishSubject.create();

    @NonNull
    private final ResourceProvider resourceProvider;

    @NonNull
    private final GlobalNotificationProvider notificationProvider;

    @NonNull
    private final DataLayer.GetBeer getBeer;

    @NonNull
    private final DataLayer.GetBeer reloadBeer;

    @NonNull
    private final DataLayer.GetUser getUser;

    @NonNull
    private final DataLayer.TickBeer tickBeer;

    @NonNull
    private final CompositeSubscription subscription = new CompositeSubscription();

    private final int beerId;

    @Inject
    public BeerDetailsViewModel(@Named("id") Integer beerId,
                                @Named("full") @NonNull DataLayer.GetBeer getBeer,
                                @Named("reload") @NonNull DataLayer.GetBeer reloadBeer,
                                @NonNull DataLayer.TickBeer tickBeer,
                                @NonNull DataLayer.GetBrewer getBrewer,
                                @Named("reload") @NonNull DataLayer.GetBrewer reloadBrewer,
                                @NonNull DataLayer.GetUser getUser,
                                @NonNull DataLayer.GetReviews getReviews,
                                @NonNull DataLayer.FetchReviews fetchReviews,
                                @NonNull DataLayer.GetReview getReview,
                                @NonNull ReviewListStore reviewListStore,
                                @NonNull ResourceProvider resourceProvider,
                                @NonNull ProgressStatusProvider progressStatusProvider,
                                @NonNull GlobalNotificationProvider notificationProvider) {
        checkNotNull(beerId);
        checkNotNull(getBeer);
        checkNotNull(reloadBeer);
        checkNotNull(tickBeer);
        checkNotNull(getBrewer);
        checkNotNull(reloadBrewer);
        checkNotNull(getUser);
        checkNotNull(getReviews);
        checkNotNull(fetchReviews);
        checkNotNull(getReview);
        checkNotNull(reviewListStore);
        checkNotNull(resourceProvider);
        checkNotNull(progressStatusProvider);
        checkNotNull(notificationProvider);

        beerViewModel = new BeerViewModel(beerId, getBeer, progressStatusProvider);
        brewerViewModel = new BrewerViewModel(-1, beerId, getBeer, getBrewer, reloadBrewer, progressStatusProvider);
        reviewListViewModel = new ReviewListViewModel(beerId, getReviews, fetchReviews, getReview, reviewListStore, progressStatusProvider);

        this.beerId = beerId;
        this.getBeer = getBeer;
        this.reloadBeer = reloadBeer;
        this.getUser = getUser;
        this.tickBeer = tickBeer;
        this.resourceProvider = resourceProvider;
        this.notificationProvider = notificationProvider;
    }

    @NonNull
    public Observable<Beer> getBeer() {
        return beerViewModel.getBeer();
    }

    @NonNull
    public Observable<Brewer> getBrewer() {
        return brewerViewModel.getBrewer();
    }

    @NonNull
    public Observable<User> getUser() {
        return getUser.call()
                .compose(RxUtils::pickValue);
    }

    @NonNull
    public Observable<List<Review>> getReviews() {
        return reviewListViewModel.getReviews();
    }

    public void loadMoreReviews(int currentReviewsCount) {
        reviewListViewModel.fetchReviews((currentReviewsCount / Constants.REVIEWS_PER_PAGE) + 1);
    }

    public void reloadBeerDetails() {
        subscription.add(reloadBeer.call(beerId)
                .subscribe(Actions.empty(), Timber::e));
    }

    public void reloadReviews() {
        reviewListViewModel.reloadReviews();
    }

    @NonNull
    public Observable<Boolean> tickSuccessStatus() {
        return tickSuccessSubject.asObservable();
    }

    public void tickBeer(int rating) {
        Observable<DataStreamNotification<Void>> observable =
                get(tickBeer).call(beerId, rating)
                        .share();

        subscription.add(getBeer.call(beerId)
                .filter(DataStreamNotification::isOnNext)
                .take(1)
                .map(DataStreamNotification::getValue)
                .subscribe(beer -> notificationProvider.addNetworkSuccessListener(observable,
                        chooseSuccessString(beer, rating),
                        resourceProvider.getString(R.string.tick_failure))));

        subscription.add(observable
                .takeUntil(DataStreamNotification::isCompleted)
                .subscribe(notification -> {
                    switch (notification.getType()) {
                        case COMPLETED_WITH_VALUE:
                        case COMPLETED_WITHOUT_VALUE:
                            tickSuccessSubject.onNext(true);
                            break;
                        case COMPLETED_WITH_ERROR:
                            tickSuccessSubject.onNext(false);
                            break;
                        case ONGOING:
                        case ON_NEXT:
                            break;
                    }
                }, error -> Timber.w(error, "Error ticking beer")));
    }

    @NonNull
    private String chooseSuccessString(@NonNull Beer beer, int rating) {
        return rating == 0
                ? resourceProvider.getString(R.string.tick_removed)
                : String.format(resourceProvider.getString(R.string.tick_success), beer.name());
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        beerViewModel.bindToDataModel();
        brewerViewModel.bindToDataModel();
        reviewListViewModel.bindToDataModel();
    }

    @Override
    protected void unbind() {
        beerViewModel.unbindDataModel();
        brewerViewModel.unbindDataModel();
        reviewListViewModel.unbindDataModel();

        subscription.clear();
    }
}
