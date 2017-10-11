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
package quickbeer.android.viewmodels;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.pojos.ItemList;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

import static io.reark.reark.utils.Preconditions.get;

public abstract class NetworkViewModel<T> extends quickbeer.android.core.viewmodel.BaseViewModel {

    public enum ProgressStatus {
        LOADING,
        ERROR,
        VALUE,
        EMPTY
    }

    @NonNull
    private final BehaviorSubject<ProgressStatus> progressStatus = BehaviorSubject.create();

    @NonNull
    public Observable<ProgressStatus> getProgressStatus() {
        return progressStatus.asObservable();
    }

    public void setProgressStatus(@NonNull ProgressStatus status) {
        progressStatus.onNext(get(status));
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    protected void unbind() {
        // No implementation
    }

    @NonNull
    Func1<DataStreamNotification<T>, ProgressStatus> toProgressStatus() {
        return notification -> {
            if (notification.isOngoing() || notification.isCompletedWithValue()) {
                return ProgressStatus.LOADING;
            } else if (notification.isCompletedWithError()) {
                return ProgressStatus.ERROR;
            } else if (notification.isOnNext() && hasValue(notification.getValue())) {
                return ProgressStatus.VALUE;
            } else {
                return ProgressStatus.EMPTY;
            }
        };
    }

    @NonNull
    static <T> Func1<DataStreamNotification<T>, ProgressStatus> toStaticProgressStatus() {
        return notification -> {
            if (notification.isOngoing() || notification.isCompletedWithValue()) {
                return ProgressStatus.LOADING;
            } else if (notification.isCompletedWithError()) {
                return ProgressStatus.ERROR;
            } else if (notification.isOnNext()) {
                return ProgressStatus.VALUE;
            } else {
                return ProgressStatus.EMPTY;
            }
        };
    }

    private boolean hasValue(@Nullable T list) {
        if (list == null) {
            return false;
        } else if (list instanceof Collection) {
            return !((Collection<?>) list).isEmpty();
        } else if (list instanceof ItemList) {
            return !((ItemList<?>) list).getItems().isEmpty();
        } else {
            return true;
        }
    }
}
