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
package quickbeer.android.next.viewmodels;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class ProgressIndicatorViewModel {
    private final static String TAG = ProgressIndicatorViewModel.class.getSimpleName();

    public enum Status {
        IDLE,
        INDEFINITE,
        LOADING;
    }

    private Subscription subscription;
    private final BehaviorSubject<List<Observable<Float>>> sourceObservables = BehaviorSubject.create();
    private final BehaviorSubject<Pair<Status, Float>> progressSubject = BehaviorSubject.create();

    public ProgressIndicatorViewModel() {
    }

    public void susbcribe() {
        if (subscription != null) {
            unsubscribe();
        }

        subscription = sourceObservables.asObservable()
                .flatMap(observableList -> {
                    return Observable.merge(observableList)
                            .reduce(new Pair<>(0f, 0), (aggregate, progress) -> {
                                return new Pair<>(aggregate.first + progress, aggregate.second + 1);
                            });
                })
                .doOnNext(aggregate -> {
                    Log.d(TAG, "Aggregate progress: " +
                            aggregate.first + " with " +
                            aggregate.second + " sources");
                })
                .map(aggregate -> {
                    final int count = aggregate.second;
                    final float progress = aggregate.first / aggregate.second;

                    if (count == 0) {
                        // Nothing in progress, though shouldn't happen
                        return new Pair<Status, Float>(Status.IDLE, 0f);
                    } else if (count == 1 && progress < 1) {
                        // One request in who knows what state. We don't receive progress
                        // notifications, so we can't show progress for a single request.
                        return new Pair<Status, Float>(Status.INDEFINITE, 0f);
                    } else {
                        // Multiple requests or done, we can actually show progress
                        return new Pair<Status, Float>(Status.LOADING, progress);
                    }
                })
                .doOnNext(progress -> {
                    if (progress.second == 1.0f) {
                        // All requests finished, clear the tables before next progress
                        sourceObservables.onNext(new ArrayList<>());
                    }
                })
                .subscribe(progressSubject);
    }

    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public Observable<Pair<Status, Float>> getProgress() {
        return progressSubject.asObservable();
    }

    public void addSourceObservable(Observable<DataStreamNotification> observable) {
        Log.d(TAG, "addDataStreamNotificationObservable");

        List<Observable<Float>> list = sourceObservables.getValue();
        list.add(observable.map(this::toProgress));
        sourceObservables.onNext(list);
    }

    private float toProgress(DataStreamNotification notification) {
        if (notification.isFetchingStart()) {
            return 0.5f;
        } else {
            return 1.0f;
        }
    }
}
