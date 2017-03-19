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
package quickbeer.android.rx;

import android.support.annotation.NonNull;

import polanski.option.Option;
import polanski.option.OptionUnsafe;
import quickbeer.android.data.pojos.ItemList;
import rx.Observable;
import rx.Single;

public final class RxUtils<T> {

    @NonNull
    public static <T> Observable<T> pickValue(@NonNull Observable<Option<T>> observable) {
        return observable.filter(Option::isSome)
                         .map(OptionUnsafe::getUnsafe);
    }

    @NonNull
    public static <T> Observable<T> valueOrError(@NonNull Observable<Option<T>> observable) {
        return observable.map(OptionUnsafe::getUnsafe);
    }

    @NonNull
    public static <T> Single<T> valueOrError(@NonNull Single<Option<T>> single) {
        return single.map(OptionUnsafe::getUnsafe);
    }

    @NonNull
    public static <T> Boolean isNoneOrEmpty(@NonNull Option<ItemList<T>> option) {
        return option.match(list -> list.getItems().isEmpty(), () -> true);
    }

    public static void nothing(Object __) {
    }

    public static boolean isTrue(boolean value) {
        return value;
    }

    public static boolean isFalse(boolean value) {
        return !value;
    }
}
