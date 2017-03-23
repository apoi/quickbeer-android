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
package quickbeer.android.features.about;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class AboutActivity extends InjectingDrawerActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkNotNull(navigationProvider);
        checkNotNull(analytics);

        setContentView(R.layout.basic_activity);

        ButterKnife.bind(this);

        setupDrawerLayout();

        setBackNavigationEnabled(true);

        toolbar.setTitle(getString(R.string.about));

        analytics.createEvent(Events.Screen.ABOUT);

        if (savedInstanceState == null) {
            navigationProvider.addPage(Page.ABOUT);
        }
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void navigateTo(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_about) {
            return;
        }

        get(navigationProvider).navigateWithNewActivity(menuItem);
    }
}
