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

import io.reark.reark.data.DataStreamNotification;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

import static io.reark.reark.utils.Preconditions.get;

public abstract class NetworkViewModel extends quickbeer.android.core.viewmodel.BaseViewModel {

    public enum ProgressStatus {
        LOADING,
        ERROR,
        IDLE
    }

    @NonNull
    private final BehaviorSubject<ProgressStatus> progressStatus = BehaviorSubject.create();

    @NonNull
    public Observable<ProgressStatus> getProgressStatus() {
        return progressStatus.asObservable();
    }

    public void setNetworkStatusText(@NonNull final ProgressStatus status) {
        progressStatus.onNext(get(status));
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    protected void unbind() {
        // No implementation
    }

    @NonNull
    static <T> Func1<DataStreamNotification<T>, ProgressStatus> toProgressStatus() {
        return notification -> {
            if (notification.isFetchingStart()) {
                return ProgressStatus.LOADING;
            } else if (notification.isFetchingError()) {
                return ProgressStatus.ERROR;
            } else {
                return ProgressStatus.IDLE;
            }
        };
    }
}
