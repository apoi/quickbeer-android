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

import java.util.Date;
import java.util.List;

public class BeerSearch {
    private final String search;
    private final List<Integer> items;
    private Date updateDate;

    public BeerSearch(final String search, final List<Integer> items, final Date updateDate) {
        this.search = search;
        this.items = items;
        this.updateDate = updateDate;
    }

    public String getSearch() {
        return search;
    }

    public List<Integer> getItems() {
        return items;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date value) {
        updateDate = value;
    }

    @Override
    public String toString() {
        return "BeerSearch{search=" + search
                + ", items='" + (items == null ? "null " : items.size())
                + ", updated='" + updateDate
                + "'}";
    }
}
