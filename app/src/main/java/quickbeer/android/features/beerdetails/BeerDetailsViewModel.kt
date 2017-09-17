/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.features.beerdetails

import io.reark.reark.data.DataStreamNotification
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.core.viewmodel.SimpleViewModel
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.actions.ReviewActions
import quickbeer.android.data.actions.UserActions
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.pojos.Review
import quickbeer.android.data.pojos.User
import quickbeer.android.data.stores.ReviewListStore
import quickbeer.android.providers.GlobalNotificationProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.rx.RxUtils
import quickbeer.android.viewmodels.BeerViewModel
import quickbeer.android.viewmodels.BrewerViewModel
import quickbeer.android.viewmodels.ReviewListViewModel
import rx.Observable
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BeerDetailsViewModel @Inject
constructor(@Named("id") private val beerId: Int,
            private val userActions: UserActions,
            private val beerActions: BeerActions,
            brewerActions: BrewerActions,
            reviewActions: ReviewActions,
            reviewListStore: ReviewListStore,
            progressStatusProvider: ProgressStatusProvider,
            private val resourceProvider: ResourceProvider,
            private val notificationProvider: GlobalNotificationProvider)
    : SimpleViewModel() {

    private val beerViewModel: BeerViewModel =
            BeerViewModel(beerId, beerActions, progressStatusProvider)

    private val brewerViewModel: BrewerViewModel =
            BrewerViewModel(-1, beerId, beerActions, brewerActions, progressStatusProvider)

    private val reviewListViewModel: ReviewListViewModel =
            ReviewListViewModel(beerId, beerActions, reviewActions, reviewListStore, progressStatusProvider)

    private val tickSuccessSubject = PublishSubject.create<Boolean>()

    private val subscription = CompositeSubscription()

    val beer: Observable<Beer>
        get() = beerViewModel.getBeer()

    val brewer: Observable<Brewer>
        get() = brewerViewModel.getBrewer()

    val user: Observable<User>
        get() {
            return userActions.getUser()
                    .compose { RxUtils.pickValue(it) }
        }

    val reviews: Observable<List<Review>>
        get() = reviewListViewModel.getReviews()

    fun loadMoreReviews(currentReviewsCount: Int) {
        reviewListViewModel.fetchReviews(currentReviewsCount / Constants.REVIEWS_PER_PAGE + 1)
    }

    fun reloadBeerDetails() {
        subscription.add(beerActions.fetch(beerId)
                .subscribe({}, { Timber.e(it) }))
    }

    fun reloadReviews() {
        reviewListViewModel.reloadReviews()
    }

    fun tickSuccessStatus(): Observable<Boolean> {
        return tickSuccessSubject.asObservable()
    }

    fun tickBeer(rating: Int) {
        val observable = beerActions.tick(beerId, rating).share()

        subscription.add(beerActions.get(beerId)
                .filter { it.isOnNext }
                .take(1)
                .map { it.value }
                .subscribe {
                    notificationProvider.addNetworkSuccessListener(observable,
                            chooseSuccessString(it!!, rating),
                            resourceProvider.getString(R.string.tick_failure))
                })

        subscription.add(observable
                .takeUntil { it.isCompleted }
                .subscribe(
                        { notification ->
                            when (notification.type) {
                                DataStreamNotification.Type.COMPLETED_WITH_VALUE, DataStreamNotification.Type.COMPLETED_WITHOUT_VALUE -> tickSuccessSubject.onNext(true)
                                DataStreamNotification.Type.COMPLETED_WITH_ERROR -> tickSuccessSubject.onNext(false)
                                DataStreamNotification.Type.ONGOING, DataStreamNotification.Type.ON_NEXT -> { }
                            }
                        },
                        { Timber.w(it, "Error ticking beer") }))
    }

    private fun chooseSuccessString(beer: Beer, rating: Int): String {
        return if (rating == 0)
            resourceProvider.getString(R.string.tick_removed)
        else
            String.format(resourceProvider.getString(R.string.tick_success), beer.name())
    }

    override fun bind(subscription: CompositeSubscription) {
        beerViewModel.bindToDataModel()
        brewerViewModel.bindToDataModel()
        reviewListViewModel.bindToDataModel()
    }

    override fun unbind() {
        beerViewModel.unbindDataModel()
        brewerViewModel.unbindDataModel()
        reviewListViewModel.unbindDataModel()

        subscription.clear()
    }
}
