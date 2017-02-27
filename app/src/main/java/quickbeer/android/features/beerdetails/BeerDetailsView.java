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
package quickbeer.android.features.beerdetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import quickbeer.android.R;
import quickbeer.android.data.pojos.Beer;

import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsView extends FrameLayout {

    private BeerDetailsAdapter beerDetailsAdapter;

    public BeerDetailsView(Context context) {
        super(context);
    }

    public BeerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBeer(@NonNull final Beer beer) {
        get(beerDetailsAdapter).setBeer(get(beer));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beerDetailsAdapter = new BeerDetailsAdapter();

        RecyclerView beerDetailsListView = (RecyclerView) findViewById(R.id.beers_details_list_view);
        beerDetailsListView.setHasFixedSize(true);
        beerDetailsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        beerDetailsListView.setAdapter(beerDetailsAdapter);
    }
}
