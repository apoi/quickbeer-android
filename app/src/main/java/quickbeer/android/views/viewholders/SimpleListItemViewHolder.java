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
package quickbeer.android.views.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import quickbeer.android.R;
import quickbeer.android.data.pojos.SimpleItem;

public class SimpleListItemViewHolder extends RecyclerView.ViewHolder {

    @NonNull
    private final TextView textView;

    @NonNull
    private final TextView iconView;

    public SimpleListItemViewHolder(@NonNull final View view,
                                    @NonNull final View.OnClickListener onClickListener) {
        super(view);

        iconView = (TextView) view.findViewById(R.id.list_item_icon);
        textView = (TextView) view.findViewById(R.id.list_item_name);

        view.setOnClickListener(onClickListener);
    }

    public void setItem(SimpleItem simpleItem) {
        iconView.setText(simpleItem.getCode());
        textView.setText(simpleItem.getName());
    }
}
