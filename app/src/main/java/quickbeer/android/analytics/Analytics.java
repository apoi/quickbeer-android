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
package quickbeer.android.analytics;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
