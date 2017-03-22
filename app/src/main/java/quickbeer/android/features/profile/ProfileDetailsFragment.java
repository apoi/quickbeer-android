/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.data.pojos.User;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class ProfileDetailsFragment extends BindingBaseFragment {

    @BindView(R.id.profile_welcome)
    TextView welcomeTextView;

    @BindView(R.id.profile_ticked_beers)
    TextView tickedBeersTextView;

    @Nullable
    @Inject
    ProfileDetailsViewModel profileDetailsViewModel;

    @Nullable
    @Inject
    Analytics analytics;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel()
                    .getUser()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ProfileDetailsFragment.this::setUser, Timber::e));

            subscription.add(viewModel()
                    .getUser()
                    .map(User::id)
                    .switchMap(viewModel()::getTicksOnce)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(ProfileDetailsFragment.this::setTickedBeers, Timber::e));
        }
    };

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        get(analytics).createViewEvent(Events.Screen.PROFILE_VIEW);
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    private void setUser(@NonNull User user) {
        welcomeTextView.setText(String.format(getString(R.string.welcome_text), user.username()));
    }

    private void setTickedBeers(@NonNull List<Integer> tickedBeers) {
        tickedBeersTextView.setText(String.format(getString(R.string.ticked_beers), tickedBeers.size()));
    }

    @NonNull
    @Override
    protected ProfileDetailsViewModel viewModel() {
        return get(profileDetailsViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

}
