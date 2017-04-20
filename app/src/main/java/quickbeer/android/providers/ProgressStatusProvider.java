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
package quickbeer.android.providers;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reark.reark.data.DataStreamNotification;
import ix.Ix;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class ProgressStatusProvider {

    public enum Status {
        IDLE,
        INDEFINITE,
        LOADING
    }

    @NonNull
    private final BehaviorSubject<ProgressStatus> progressSubject = BehaviorSubject.create(ProgressStatus.IDLE);

    @NonNull
    private final ConcurrentMap<Integer, Float> progressMap = new ConcurrentHashMap<>(20, 0.75f, 4);

    private int nextId = 1;

    public Subscription addProgressObservable(@NonNull Observable<DataStreamNotification<?>> notificationObservable) {
        int progressId = createId();

        return notificationObservable
                .observeOn(Schedulers.computation())
                .filter(observable -> !observable.isOnNext()) // Only interested in status changes
                .map(ProgressStatusProvider::toProgress)
                .doOnUnsubscribe(() -> finishProgress(progressId))
                .subscribe(progress -> addProgress(progressId, progress), Timber::w);
    }

    @NonNull
    public Observable<ProgressStatus> progressStatus() {
        return progressSubject.asObservable()
                .onBackpressureBuffer()
                .distinctUntilChanged();
    }

    private int createId() {
        return nextId++;
    }

    private void addProgress(int identifier, float progress) {
        Timber.v("addProgress(%s, %s)", identifier, progress);

        // Skip progress update if it's going straight to finished
        if (progressMap.containsKey(identifier) || progress < 1.0) {
            progressMap.put(identifier, progress);
            aggregate();
        }
    }

    private void finishProgress(int identifier) {
        Timber.v("finishProgress(%s)", identifier);

        progressMap.remove(identifier);
        aggregate();
    }

    private static float toProgress(@NonNull DataStreamNotification<?> notification) {
        //noinspection MagicNumber
        return notification.isOngoing() ? 0.5f : 1.0f;
    }

    private synchronized void aggregate() {
        ProgressStatus aggregate = ProgressStatus.IDLE;
        Collection<Float> values = progressMap.values();
        int count = values.size();

        if (count > 0) {
            float sum = Ix.from(values).reduce((v1, v2) -> v1 + v2).first();
            float progress = count > 0 ? sum / count : 0;
            aggregate = createStatus(count, progress);
        }

        progressSubject.onNext(aggregate);

        // Status is idle when all progresses are idle, and this aggregate has finished.
        if (aggregate.status() == Status.IDLE) {
            progressMap.clear();
        }
    }

    @SuppressWarnings("IfStatementWithTooManyBranches")
    private static ProgressStatus createStatus(int count, float progress) {
        if (count == 0) {
            // Nothing in progress, though shouldn't happen
            return ProgressStatus.IDLE;
        } else if (count == 1 && progress < 1) {
            // One request in who knows what state. We don't receive progress
            // notifications, so we can't show progress for a single request.
            return ProgressStatus.INDEFINITE;
        } else if (progress < 1) {
            // Multiple requests or done, we can actually show progress
            return ProgressStatus.create(Status.LOADING, progress);
        } else {
            // Finished, back to idle
            return ProgressStatus.IDLE;
        }
    }

    @SuppressWarnings("EqualsAndHashcode")
    @AutoValue
    public abstract static class ProgressStatus {

        public static final ProgressStatus IDLE = ProgressStatus.create(Status.IDLE, 0.0f);

        public static final ProgressStatus INDEFINITE = ProgressStatus.create(Status.INDEFINITE, 0.0f);

        @NonNull
        public abstract Status status();
        public abstract float progress();

        @NonNull
        public static ProgressStatus create(@NonNull Status status, float progress) {
            return new AutoValue_ProgressStatusProvider_ProgressStatus(status, progress);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof ProgressStatus) {
                ProgressStatus other = (ProgressStatus) o;
                return (status() == other.status() &&
                        (status() != Status.LOADING // Idle and indefinite equal regardless of progress
                                || (Float.floatToIntBits(progress()) == Float.floatToIntBits(other.progress()))));
            }
            return false;
        }
    }
}
