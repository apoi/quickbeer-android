package quickbeer.android.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import quickbeer.android.Constants.Preferences;

public class PreferencesProvider {

    @NonNull
    private final SharedPreferences sharedPreferences;

    public PreferencesProvider(@NonNull Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstRunDrawerShown() {
        return sharedPreferences.getBoolean(Preferences.FIRST_RUN_DRAWER, false);
    }

    public void setIsFirstRunDrawerShown(boolean value) {
        sharedPreferences.edit()
                .putBoolean(Preferences.FIRST_RUN_DRAWER, true)
                .apply();
    }

}
