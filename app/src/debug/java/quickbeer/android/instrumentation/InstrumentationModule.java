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
package quickbeer.android.instrumentation;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import quickbeer.android.injections.ForApplication;
import timber.log.Timber;

@Module
public final class InstrumentationModule {

    @Provides
    @Singleton
    static Timber.Tree providesLoggingTree() {
        return new Timber.DebugTree();
    }

    @Provides
    @Singleton
    public static ApplicationInstrumentation provideInstrumentation(
            LeakTracing leakTracing,
            @Named("networkInstrumentation") Instrumentation networkInstrumentation) {
        return new DebugApplicationInstrumentation(leakTracing, networkInstrumentation);
    }

    @Provides
    @Singleton
    public static LeakTracing provideLeakTracing(Application application) {
        return new LeakCanaryTracing(application);
    }

    @Provides
    @Singleton
    @Named("networkInstrumentation")
    public static Instrumentation provideStethoInstrumentation(@ForApplication Context context) {
        return new StethoInstrumentation(context);
    }

    @Provides
    @Singleton
    @Named("networkInterceptors")
    public static List<Interceptor> provideNetworkInterceptors() {
        return Collections.singletonList(new StethoInterceptor());
    }
}
