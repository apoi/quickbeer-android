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
package quickbeer.android.next.activities.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.injections.ApplicationGraph;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {
    // Composite for subscriptions meant to stay alive for the activity's duration
    protected CompositeSubscription activitySubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
    }

    @Override
    protected void onDestroy() {
        activitySubscription.clear();

        super.onDestroy();
    }

    public ApplicationGraph getGraph() {
        return QuickBeer.getInstance().getGraph();
    }

    protected void inject() {
    }
}
