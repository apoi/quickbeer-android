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
package quickbeer.android.data;

import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import polanski.option.Option;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.Review;
import quickbeer.android.data.pojos.User;
import rx.Observable;

@SuppressWarnings("MarkerInterface")
public interface DataLayer {

    interface Login {
        Observable<DataStreamNotification<User>> call(@NonNull String username, @NonNull String password);
    }

    interface GetLoginStatus {
        @NonNull
        Observable<DataStreamNotification<User>> call();
    }

    interface GetUser {
        @NonNull
        Observable<Option<User>> call();
    }

    interface SetUser {
        void call(@NonNull User user);
    }

    interface GetBeer {
        @NonNull
        Observable<DataStreamNotification<Beer>> call(int beerId, boolean fullDetails);
    }

    interface AccessBeer {
        void call(int beerId);
    }

    interface GetAccessedBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }

    interface AccessBrewer {
        void call(int brewerId);
    }

    interface GetAccessedBrewers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }

    interface GetBeerSearchQueries {
        @NonNull
        Observable<List<String>> call();
    }

    interface GetBeerSearch {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String search);
    }

    interface GetBarcodeSearch {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String barcode);
    }

    interface GetTopBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call();
    }

    interface GetBeersInCountry {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String countryId);
    }

    interface GetBeersInStyle {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String styleId);
    }

    interface GetReview {
        @NonNull
        Observable<Option<Review>> call(int reviewId);
    }

    interface GetReviews {
        @NonNull
        Observable<DataStreamNotification<ItemList<Integer>>> call(int beerId);
    }

    interface FetchReviews {
        void call(int beerId, int page);
    }

    interface FetchTickedBeers {
        void call(String userId);
    }

    interface TickBeer {
        @NonNull
        Observable<DataStreamNotification<Void>> call(int beerId, int rating);
    }

    interface GetTickedBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(String userId);
    }

    interface GetBrewer {
        @NonNull
        Observable<DataStreamNotification<Brewer>> call(int brewerId);
    }

    interface GetBrewerBeers {
        @NonNull
        Observable<DataStreamNotification<ItemList<String>>> call(@NonNull String brewerId);
    }

}
