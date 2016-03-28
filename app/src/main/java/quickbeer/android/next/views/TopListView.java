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
package quickbeer.android.next.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.activities.CountryListActivity;
import quickbeer.android.next.activities.StyleListActivity;
import quickbeer.android.next.activities.TopBeersActivity;
import quickbeer.android.next.adapters.MenuListAdapter;
import quickbeer.android.next.views.listitems.ActivityLaunchItem;

public class TopListView extends BeerListView {
    private static final String TAG = TopListView.class.getSimpleName();

    private RecyclerView menuListView;
    private MenuListAdapter menuListAdapter;
    private List<ActivityLaunchItem> activityLaunchItems = new ArrayList<>();

    public TopListView(Context context) {
        super(context);
    }

    public TopListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getMenuItemCount() {
        return 3;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        activityLaunchItems.add(new ActivityLaunchItem("best worldwide", R.drawable.ic_icon_globe, TopBeersActivity.class));
        activityLaunchItems.add(new ActivityLaunchItem("best by country", R.drawable.ic_icon_number_1, CountryListActivity.class));
        activityLaunchItems.add(new ActivityLaunchItem("best by style", R.drawable.ic_icon_number_2, StyleListActivity.class));

        menuListAdapter = new MenuListAdapter(activityLaunchItems);
        menuListView = (RecyclerView) findViewById(R.id.menu_list_view);
        menuListView.setHasFixedSize(true);
        menuListView.setLayoutManager(new NoScrollingLayoutManager(getContext()));
        menuListView.setAdapter(menuListAdapter);

        // Redirect unhandled click events to menu
        getListView().setOnTouchListener((view, event) -> {
            menuListView.dispatchTouchEvent(event);
            return false;
        });

        MaskingLayout menuListMask = ((MaskingLayout) findViewById(R.id.menu_list_mask));
        menuListMask.setScrollingMask(getListView());
    }
}
