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
package quickbeer.android.features.list.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import javax.inject.Inject;

import polanski.option.Option;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.data.pojos.Session;
import quickbeer.android.features.profile.ProfileActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.viewmodels.NetworkViewModel;
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus;
import quickbeer.android.viewmodels.TickedBeersViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class TickedBeersFragment extends BeerListFragment {

    @Nullable
    @Inject
    TickedBeersViewModel tickedBeersViewModel;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    Session session;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            TickedBeersFragment.super.dataBinder().bind(subscription);

            checkNotNull(session);

            subscription.add(viewModel()
                    .getUser()
                    .first()
                    .filter(Option::isNone)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(__ -> showLoginDialog(), Timber::e));

            subscription.add(viewModel()
                    .getUser()
                    .first()
                    .compose(RxUtils::pickValue)
                    .filter(__ -> !session.isTicksRequested())
                    .doOnNext(__ -> session.setTicksRequested(true))
                    .subscribe(user -> viewModel().refreshTicks(user), Timber::e));
        }

        @Override
        public void unbind() {
            TickedBeersFragment.super.dataBinder().unbind();
        }
    };

    @Override
    protected void inject() {
        super.inject();

        getComponent().inject(this);
    }

    @NonNull
    @Override
    protected String toStatusValue(@NonNull ProgressStatus progressStatus) {
        return progressStatus == NetworkViewModel.ProgressStatus.EMPTY
                ? getString(R.string.ticks_empty_list)
                : super.toStatusValue(progressStatus);
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.login_dialog_title)
                .setMessage(R.string.login_to_view_ratings_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> navigateToLogin())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    private void navigateToLogin() {
        Timber.d("navigateToLogin");

        Intent intent = new Intent(getContext(), ProfileActivity.class);
        getContext().startActivity(intent);
    }

    @NonNull
    @Override
    protected TickedBeersViewModel viewModel() {
        return get(tickedBeersViewModel);
    }

    @Override
    protected void onQuery(@NonNull String query) {
        get(navigationProvider).triggerSearch(query);
    }

    @Override
    @NonNull
    protected DataBinder dataBinder() {
        return dataBinder;
    }
}
