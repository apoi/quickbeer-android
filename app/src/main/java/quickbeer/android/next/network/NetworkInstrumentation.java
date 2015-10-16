package quickbeer.android.next.network;

import android.support.annotation.NonNull;

import quickbeer.android.next.utils.Instrumentation;

public interface NetworkInstrumentation<T> extends Instrumentation {
    @NonNull
    T decorateNetwork(@NonNull final T httpClient);
}
