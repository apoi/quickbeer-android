package quickbeer.android.next.viewmodels;

import android.support.annotation.NonNull;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Preconditions;
import io.reark.reark.viewmodels.AbstractViewModel;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Created by antti on 27.10.2015.
 */
public abstract class BaseViewModel extends AbstractViewModel {

    public enum ProgressStatus {
        LOADING, ERROR, IDLE
    }

    protected final BehaviorSubject<ProgressStatus> networkRequestStatusText = BehaviorSubject.create();

    @NonNull
    public Observable<ProgressStatus> getNetworkRequestStatus() {
        return networkRequestStatusText.asObservable();
    }

    void setNetworkStatusText(@NonNull ProgressStatus status) {
        Preconditions.checkNotNull(status, "ProgressStatus cannot be null.");

        networkRequestStatusText.onNext(status);
    }

    @NonNull
    static Func1<DataStreamNotification, ProgressStatus> toProgressStatus() {
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
