package quickbeer.android.providers;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reark.reark.data.DataStreamNotification;
import ix.Ix;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class ProgressStatusProvider {

    public enum Status {
        IDLE,
        INDEFINITE,
        LOADING
    }

    @NonNull
    private final CompositeSubscription subscription = new CompositeSubscription();

    @NonNull
    private final BehaviorSubject<Pair<Status, Float>> progressSubject = BehaviorSubject.create(Pair.create(Status.IDLE, 0.0f));

    @NonNull
    private final ConcurrentMap<Integer, Float> progressMap = new ConcurrentHashMap<>(20, 0.75f, 4);

    private int nextId = 1;

    /**
     * Subscribes to the progress observable. This means that any observable passed here will be
     * kept subscribed until its completion. Idea is that since a requests won't be canceled anyway,
     * it makes sense to also keep the subscription to follow status even if application is
     * suspended.
     *
     * Long-living progress observables should not be aggregated, or at least they should be
     * explicitly finished in a controlled manner if app is sent to background.
     */
    public Subscription addProgressObservable(@NonNull Observable<DataStreamNotification<?>> notificationObservable) {
        int progressId = createId();

        return notificationObservable
                .observeOn(Schedulers.computation())
                .filter(observable -> !observable.isOnNext()) // Only interested in status changes
                .doOnEach(notification -> Timber.d("%s= progress change: %s", progressId, notification))
                .map(ProgressStatusProvider::toProgress)
                .doOnUnsubscribe(() -> finishProgress(progressId))
                .subscribe(progress -> addProgress(progressId, progress), Timber::w);
    }

    @NonNull
    public Observable<Pair<Status, Float>> progressStatus() {
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
        Pair<Status, Float> aggregate = Pair.create(Status.IDLE, 0.0f);
        Collection<Float> values = progressMap.values();
        int count = values.size();

        if (count > 0) {
            float sum = Ix.from(values).reduce((v1, v2) -> v1 + v2).first();
            float progress = count > 0 ? sum / count : 0;
            aggregate = createStatus(count, progress);
        }

        progressSubject.onNext(aggregate);

        // Status is idle when all progresses are idle, and this aggregate has finished.
        if (aggregate.first == Status.IDLE) {
            progressMap.clear();
        }
    }

    @SuppressWarnings("IfStatementWithTooManyBranches")
    private static Pair<Status, Float> createStatus(int count, float progress) {
        if (count == 0) {
            // Nothing in progress, though shouldn't happen
            return Pair.create(Status.IDLE, 0.0f);
        } else if (count == 1 && progress < 1) {
            // One request in who knows what state. We don't receive progress
            // notifications, so we can't show progress for a single request.
            return Pair.create(Status.INDEFINITE, 0.0f);
        } else if (progress < 1) {
            // Multiple requests or done, we can actually show progress
            return Pair.create(Status.LOADING, progress);
        } else {
            // Finished, back to idle
            return Pair.create(Status.IDLE, 0.0f);
        }
    }

}
