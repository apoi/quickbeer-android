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
package quickbeer.android;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import quickbeer.android.injections.ApplicationModule;
import quickbeer.android.injections.DaggerGraph;
import quickbeer.android.injections.Graph;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class QuickBeer extends Application {

    @Nullable
    private Graph graph;

    @Inject
    @Nullable
    Timber.Tree loggingTree;

    @Override
    public void onCreate() {
        super.onCreate();

        inject();

        initLogging();

        initLeakCanary();

        initDateAndTime();
    }

    @NonNull
    public Graph graph() {
        return get(graph);
    }

    private void inject() {
        if (graph == null) {
            graph = DaggerGraph.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
            graph.inject(this);
        }
    }

    private void initLogging() {
        Timber.uprootAll();
        Timber.plant(get(loggingTree));
    }

    private void initLeakCanary() {
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    private void initDateAndTime() {
        JodaTimeAndroid.init(this);
    }

}
