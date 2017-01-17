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
package quickbeer.android.data.pojos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemList<T> {

    @Nullable
    private final T key;

    @NonNull
    private final List<Integer> items;

    @Nullable
    private DateTime updateDate;

    public ItemList(@Nullable final T key,
                    @NonNull final List<Integer> items,
                    @Nullable final DateTime updateDate) {
        this.key = key;
        this.items = new ArrayList<>(items);
        this.updateDate = updateDate;
    }

    @NonNull
    public static <T> ItemList<T> create(@Nullable final T key,
                                         @NonNull final List<Integer> items,
                                         @Nullable final DateTime updateDate) {
        return new ItemList<>(key, items, updateDate);
    }

    @NonNull
    public static <T> ItemList<T> create(@NonNull final List<Integer> items) {
        return new ItemList<>(null, items, null);
    }

    @Nullable
    public T getKey() {
        return key;
    }

    @NonNull
    public List<Integer> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Nullable
    public DateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(@Nullable final DateTime value) {
        updateDate = value;
    }

    @Override
    public String toString() {
        return "ItemList{key=" + key
                + ", items='" + items.size()
                + ", updated='" + updateDate
                + "'}";
    }
}
