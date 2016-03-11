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
package quickbeer.android.next.views.listitems;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import quickbeer.android.next.R;

public class ActivityLaunchItem {
    private int icon;
    private String text;
    private Class target;
    private Integer launchType;
    private String launchKey;

    public ActivityLaunchItem(String text, int icon, Class target) {
        this.text = text;
        this.icon = icon;
        this.target = target;
    }

    public ActivityLaunchItem(String text, int icon, Class target, int launchType) {
        this.text = text;
        this.icon = icon;
        this.target = target;
        this.launchType = launchType;
    }

    public ActivityLaunchItem(String text, int icon, Class target, int launchType, String launchKey) {
        this.text = text;
        this.icon = icon;
        this.target = target;
        this.launchType = launchType;
        this.launchKey = launchKey;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView iconView;
        private ActivityLaunchItem launchItem;

        public MenuViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.menu_text);
            iconView = (ImageView) view.findViewById(R.id.menu_icon);

            view.setOnClickListener(this::launchActivity);
        }

        public void setItem(ActivityLaunchItem item) {
            launchItem = item;
            textView.setText(item.text);
            iconView.setImageResource(item.icon);
        }

        private void launchActivity(View view) {
            Intent intent = new Intent(view.getContext(), launchItem.target);

            if (launchItem.launchType != null) {
                intent.putExtra("launchType", launchItem.launchType);
            }

            if (launchItem.launchKey != null) {
                intent.putExtra("launchKey", launchItem.launchKey);
            }

            view.getContext().startActivity(intent);
        }
    }
}
