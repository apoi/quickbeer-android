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

import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;

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
    static NetworkInstrumentation providesNetworkInstrumentation(
            @NonNull StethoInstrumentation instrumentation) {
        return instrumentation;
    }

    @Provides
    @Singleton
    static StethoInstrumentation providesStethoInstrumentation(
            @ForApplication @NonNull Context context,
            @NonNull Interceptor interceptor) {
        return new StethoInstrumentation(context, interceptor);
    }

    @Provides
    @Singleton
    static Interceptor providesInterceptor() {
        return new StethoInterceptor();
    }
}
