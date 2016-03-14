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
package quickbeer.android.next.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.views.listitems.ActivityLaunchItem;
import quickbeer.android.next.views.listitems.ItemType;

public class MenuListAdapter extends BaseListAdapter<ActivityLaunchItem.ViewHolder> {
    private final List<ActivityLaunchItem> items;

    public MenuListAdapter(List<ActivityLaunchItem> items) {
        this.items = items;
    }

    @Override
    public ActivityLaunchItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
        return new ActivityLaunchItem.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ActivityLaunchItem.ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return ItemType.MENU.ordinal();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
