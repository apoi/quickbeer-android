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

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.pojos.base.MetadataAware;
import rx.functions.Func1;

import static io.reark.reark.data.DataStreamNotification.Type.ON_NEXT;

/**
 * Class to be used with Observable.distinctUntilChanged. This passes items only when they
 * differ from the previously passed item with their non-metadata parts. Metadata is identified
 * with the MetadataAware interface that the streamed classes should implement.
 */
public class DistinctiveTracker<T extends MetadataAware<T>> implements Func1<DataStreamNotification<T>, Integer> {

    private int counter = 0; // Key object for indicating distinction
    private DataStreamNotification<T> previous;

    @Override
    public Integer call(DataStreamNotification<T> notification) {
        if (isDistinctive(notification)) {
            previous = notification;
            counter++;
        }

        return counter;
    }

    private boolean isDistinctive(DataStreamNotification<T> notification) {
        if (previous == null) {
            return true;
        }

        if (notification.getType() != previous.getType()) {
            return true;
        }

        if (notification.getType() != ON_NEXT) {
            return true;
        }

        final T first = notification.getValue();
        final T second = previous.getValue();

        return (first == null || second == null) || !first.dataEquals(second);
    }
}
