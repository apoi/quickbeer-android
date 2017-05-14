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
package quickbeer.android.data.stores.cores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reark.reark.data.stores.cores.MemoryStoreCore;
import ix.Ix;
import quickbeer.android.R;
import quickbeer.android.data.pojos.BeerStyle;
import quickbeer.android.providers.ResourceProvider;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerStyleStoreCore extends MemoryStoreCore<Integer, BeerStyle> {

    @NonNull
    private final ResourceProvider resourceProvider;

    @NonNull
    private final Gson gson;

    public BeerStyleStoreCore(@NonNull ResourceProvider resourceProvider, @NonNull Gson gson) {
        super(BeerStyle::merge);

        this.resourceProvider = get(resourceProvider);
        this.gson = get(gson);

        initialize();
    }

    @SuppressWarnings("EmptyClass")
    private void initialize() {
        try {
            InputStream input = resourceProvider.openRawResource(R.raw.styles);
            Reader reader = new InputStreamReader(input, "UTF-8");

            Type listType = new TypeToken<ArrayList<BeerStyle>>(){}.getType();
            List<BeerStyle> styleList = gson.fromJson(reader, listType);

            Ix.from(styleList)
                    .subscribe(beerStyle -> put(beerStyle.id(), beerStyle));

        } catch (UnsupportedEncodingException e) {
            Timber.e(e, "Failed reading styles!");
        }
    }
}
