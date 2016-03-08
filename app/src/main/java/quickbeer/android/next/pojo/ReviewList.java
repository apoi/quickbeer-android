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
package quickbeer.android.next.pojo;

import java.util.List;

public class ReviewList {
    final private int beerId;
    final private List<Integer> items;

    public ReviewList(final int beerId, final List<Integer> items) {
        this.beerId = beerId;
        this.items = items;
    }

    public int getBeerId() {
        return beerId;
    }

    public List<Integer> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "ReviewList{beerId=" + beerId
                + ", items='" + (items == null ? "null " : items.size())
                + "'}";
    }
}
