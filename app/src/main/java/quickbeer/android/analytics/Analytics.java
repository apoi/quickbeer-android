package quickbeer.android.analytics;

import com.google.firebase.analytics.FirebaseAnalytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

public final class Analytics {

    @NonNull
    private final FirebaseAnalytics firebaseAnalytics;

    public Analytics(@NonNull Context context) {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void createViewEvent(@NonNull Events.Screen screen) {
        Bundle params = new Bundle();
        params.putString("view_screen", screen.toString());
        firebaseAnalytics.logEvent("screen_name", params);
    }

    public void createActionEvent(@NonNull Events.Action action) {
        Bundle params = new Bundle();
        params.putString("action", action.toString());
        firebaseAnalytics.logEvent("action_event", params);
    }

    public void createLaunchActionEvent(@NonNull Events.LaunchAction action) {
        Bundle params = new Bundle();
        params.putString("launch_action", action.toString());
        firebaseAnalytics.logEvent("target", params);
    }

}
