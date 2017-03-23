package quickbeer.android.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

public final class Analytics {

    @NonNull
    private final FirebaseAnalytics firebaseAnalytics;

    public Analytics(@NonNull Context context) {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void createEvent(@NonNull Events.Screen screen) {
        firebaseAnalytics.logEvent(screen.toString(), new Bundle());
    }

    public void createEvent(@NonNull Events.Entry entry) {
        firebaseAnalytics.logEvent(entry.toString(), new Bundle());
    }

    public void createEvent(@NonNull Events.Action action) {
        firebaseAnalytics.logEvent(action.toString(), new Bundle());
    }

    public void createEvent(@NonNull Events.LaunchAction action) {
        firebaseAnalytics.logEvent(action.toString(), new Bundle());
    }

}
