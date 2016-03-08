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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import quickbeer.android.next.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView header;

    public HeaderViewHolder(View view) {
        super(view);

        this.header = (TextView) view.findViewById(R.id.header_text);

        // Prevent click-through on the header area
        view.findViewById(R.id.header_area).setOnTouchListener((v, event) -> {
            return true;
        });
    }

    public void setHeader(String value) {
        header.setText(value);
    }
}
