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
package quickbeer.android.features.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.features.about.AboutActivity;
import quickbeer.android.features.list.ListActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class HomeActivity extends ListActivity {

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @NonNull
    @Override
    protected Page defaultPage() {
        return Page.HOME;
    }

    @Override
    protected boolean initialBackNavigationEnabled() {
        getSupportFragmentManager().addOnBackStackChangedListener(() ->
                setBackNavigationEnabled(get(navigationProvider).canNavigateBack()));

        return false;
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void navigateTo(@NonNull MenuItem menuItem) {
        checkNotNull(navigationProvider);

        if (menuItem.getItemId() == R.id.nav_home) {
            navigationProvider.navigateAllBack();
        } else {
            super.navigateTo(menuItem);
        }
    }
}
