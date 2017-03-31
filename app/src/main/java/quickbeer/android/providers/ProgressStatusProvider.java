package quickbeer.android.providers;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import io.reark.reark.data.DataStreamNotification;
import ix.Ix;
import rx.Observable;
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

    public int createId() {
        return nextId++;
    }

    /**
     * Subscribes to the progress observable. This means that any observable passed here will be
     * kept subscribed until its completion. Idea is that since a requests won't be canceled anyway,
     * it makes sense to also keep the subscription to follow status even if application is
     * suspended.
     *
     * Long-living progress observables should not be aggregated, or at least they should be
     * explicitly finished in a controlled manner if app is sent to background.
     */
    public void addProgressObservable(@NonNull Observable<DataStreamNotification<?>> notificationObservable) {
        int progressId = createId();

        subscription.add(notificationObservable
                .map(ProgressStatusProvider::toProgress)
                .doOnCompleted(() -> finishProgress(progressId))
                .subscribe(progress -> addProgress(progressId, progress), Timber::w));
    }

    public void addProgress(int identifier, float progress) {
        Timber.v("addProgress(%s, %s)", identifier, progress);

        progressMap.put(identifier, progress);
        aggregate();
    }

    public void finishProgress(int identifier) {
        Timber.v("finishProgress(%s)", identifier);

        progressMap.remove(identifier);
        aggregate();
    }

    @NonNull
    public Observable<Pair<Status, Float>> progressStatus() {
        return progressSubject.asObservable();
    }

    private static float toProgress(@NonNull DataStreamNotification<?> notification) {
        //noinspection MagicNumber
        return notification.isOngoing() ? 0.5f : 1.0f;
    }

    private void aggregate() {
        Collection<Float> values = progressMap.values();
        float progress = Ix.from(values).reduce((v1, v2) -> v1 + v2).first();
        Pair<Status, Float> aggregate = createStatus(values.size(), progress);

        Timber.d("Progress aggregate: " + aggregate);
        progressSubject.onNext(aggregate);
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
            return Pair.create(Status.IDLE, 1.0f);
        }
    }

}
