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
package quickbeer.android.next.views.progressindicator;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import io.reark.reark.data.DataStreamNotification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;

public class ProgressIndicatorController {
    private final static String TAG = ProgressIndicatorController.class.getSimpleName();

    public enum Status {
        IDLE,
        INDEFINITE,
        LOADING;
    }

    private ProgressIndicatorBar progressIndicatorBar;
    private List<Subscription> dataStreamObservables;
    private final BehaviorSubject<List<Observable<DataStreamNotification>>> observables = BehaviorSubject.create();
    private final List<Subscription> subscriptions = new ArrayList<>();

    public ProgressIndicatorController() {
        subscriptions.add(
                observables.asObservable()
                        .flatMap(observableList -> {
                            return Observable.merge(observableList)
                                    .reduce(new Pair<>(0f, 0), (aggregate, notification) -> {
                                        return new Pair<>(aggregate.first + getProgress(notification), aggregate.second + 1);
                                    });
                        })
                        .subscribe(aggregate -> {
                            if (progressIndicatorBar == null) {
                                return;
                            }

                            final int count = aggregate.second;
                            final float progress = aggregate.first / aggregate.second;

                            if (count == 0) {
                                // Nothing in progress, though shouldn't happen
                                progressIndicatorBar.setProgress(Status.IDLE, 0);
                            } else if (count == 1 && progress < 1) {
                                // One request in who knows what state. We don't receive progress
                                // notifications, so we can't show progress for a single request.
                                progressIndicatorBar.setProgress(Status.INDEFINITE, 0);
                            } else {
                                // Multiple requests or done, we can actually show progress
                                progressIndicatorBar.setProgress(Status.LOADING, progress);
                            }

                            if (progress == 1.0f) {
                                // All requests finished, clear the tables before next progress
                                observables.onNext(new ArrayList<>());
                            }
                        }));
    }

    private static float getProgress(DataStreamNotification notification) {
        if (notification.isFetchingStart()) {
            return 0.5f;
        } else {
            return 1.0f;
        }
    }

    public void setProgressIndicatorBar(ProgressIndicatorBar progressIndicatorBar) {
        this.progressIndicatorBar = progressIndicatorBar;
    }

    public void addDataStreamNotificationObservable(Observable<DataStreamNotification> observable) {
        List<Observable<DataStreamNotification>> list = observables.getValue();
        list.add(observable);
        observables.onNext(list);
        subscriptions.add(observable.subscribe());
    }
}
