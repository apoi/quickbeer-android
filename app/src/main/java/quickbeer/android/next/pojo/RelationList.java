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

public class RelationList {
    private final int parentId;
    private final List<Integer> items;
    private Date updateDate;

    public RelationList(final int parentId, final List<Integer> items, final Date updateDate) {
        this.parentId = parentId;
        this.items = items;
        this.updateDate = updateDate;
    }

    public int getParentId() {
        return parentId;
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
        return "RelationList{parentId=" + parentId
                + ", items='" + (items == null ? "null " : items.size())
                + ", updated='" + updateDate
                + "'}";
    }
}
