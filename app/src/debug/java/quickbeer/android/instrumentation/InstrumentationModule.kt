/**
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.instrumentation

import android.app.Application
import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import quickbeer.android.injections.ForApplication
import timber.log.Timber
import javax.inject.Named
import javax.inject.Singleton

@Module
class InstrumentationModule {

    @Provides
    @Singleton
    internal fun provideLoggingTree(): Timber.Tree {
        return Timber.DebugTree()
    }

    @Provides
    @Singleton
    fun provideInstrumentation(
        leakTracing: LeakTracing,
        @Named("networkInstrumentation") networkInstrumentation: Instrumentation
    ): ApplicationInstrumentation {
        return DebugApplicationInstrumentation(leakTracing, networkInstrumentation)
    }

    @Provides
    @Singleton
    fun provideLeakTracing(application: Application): LeakTracing {
        return LeakCanaryTracing(application)
    }

    @Provides
    @Singleton
    @Named("networkInstrumentation")
    fun provideStethoInstrumentation(@ForApplication context: Context): Instrumentation {
        return StethoInstrumentation(context)
    }

    @Provides
    @Singleton
    @Named("networkInterceptors")
    fun provideNetworkInterceptors(): List<Interceptor> {
        return listOf<Interceptor>(StethoInterceptor())
    }
}
