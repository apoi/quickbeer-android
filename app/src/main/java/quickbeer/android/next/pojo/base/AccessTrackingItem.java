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
package quickbeer.android.next.pojo.base;

import java.util.Date;

public abstract class AccessTrackingItem<T extends AccessTrackingItem> extends BasePojo<T> implements MetadataAware<T> {
    protected Date updateDate;
    protected Date accessDate;

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date date) {
        updateDate = date;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date date) {
        accessDate = date;
    }

    public abstract boolean dataEquals(T other);

    public boolean metadataEquals(T other) {
        if (updateDate != null ? !updateDate.equals(other.updateDate) : other.updateDate != null) return false;
        if (accessDate != null ? !accessDate.equals(other.accessDate) : other.accessDate != null) return false;

        return true;
    }
}
