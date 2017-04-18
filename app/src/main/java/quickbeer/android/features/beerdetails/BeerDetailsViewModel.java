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

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.pojos.User;
import quickbeer.android.providers.GlobalNotificationProvider;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.viewmodels.BrewerViewModel;
import quickbeer.android.viewmodels.ReviewListViewModel;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

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
    private final DataLayer.GetUser getUser;

    @NonNull
    private final DataLayer.TickBeer tickBeer;

    @NonNull
    private final CompositeSubscription subscription = new CompositeSubscription();

    private int beerId;

    public BeerDetailsViewModel(@NonNull DataLayer.GetBeer getBeer,
                                @NonNull DataLayer.TickBeer tickBeer,
                                @NonNull DataLayer.GetBrewer getBrewer,
                                @NonNull DataLayer.GetUser getUser,
                                @NonNull DataLayer.GetReviews getReviews,
                                @NonNull DataLayer.GetReview getReview,
                                @NonNull ResourceProvider resourceProvider,
                                @NonNull ProgressStatusProvider progressStatusProvider,
                                @NonNull GlobalNotificationProvider notificationProvider) {
        beerViewModel = new BeerViewModel(get(getBeer), get(progressStatusProvider), true);
        brewerViewModel = new BrewerViewModel(get(getBeer), get(getBrewer), get(progressStatusProvider));
        reviewListViewModel = new ReviewListViewModel(get(getReviews), get(getReview));

        this.getBeer = get(getBeer);
        this.getUser = get(getUser);
        this.tickBeer = get(tickBeer);
        this.resourceProvider = get(resourceProvider);
        this.notificationProvider = get(notificationProvider);
    }

    public void setBeerId(int beerId) {
        this.beerId = beerId;

        beerViewModel.setBeerId(beerId);
        brewerViewModel.setBeerId(beerId);
        reviewListViewModel.setBeerId(beerId);
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

    @NonNull
    public Observable<Boolean> tickSuccessStatus() {
        return tickSuccessSubject.asObservable();
    }

    public void loadMoreReviews(int currentReviewsCount) {
        // TODO load more reviews here
    }

    public void tickBeer(int rating) {
        Observable<DataStreamNotification<Void>> observable =
                get(tickBeer).call(beerId, rating)
                        .share();

        subscription.add(getBeer.call(beerId, false)
                .filter(DataStreamNotification::isOnNext)
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
