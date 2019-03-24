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
package quickbeer.android.injections

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import quickbeer.android.BuildConfig
import quickbeer.android.data.pojos.Session
import quickbeer.android.providers.GlobalNotificationProvider
import quickbeer.android.providers.PreferencesProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.providers.ToastProvider
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    @ForApplication
    internal fun provideApplicationContext(): Context {
        return application
    }

    @Provides
    @Singleton
    internal fun provideApplication(): Application {
        return application
    }

    @Provides
    @Singleton
    internal fun provideSession(): Session {
        return Session()
    }

    @Provides
    @Singleton
    internal fun provideUserProvider(@ForApplication context: Context): ResourceProvider {
        return ResourceProvider(context)
    }

    @Provides
    @Singleton
    internal fun provideToastProvider(@ForApplication context: Context): ToastProvider {
        return ToastProvider(context)
    }

    @Provides
    @Singleton
    internal fun provideProgressStatusProvider(): ProgressStatusProvider {
        return ProgressStatusProvider()
    }

    @Provides
    @Singleton
    internal fun provideGlobalNotificationProvider(toastProvider: ToastProvider): GlobalNotificationProvider {
        return GlobalNotificationProvider(toastProvider)
    }

    @Provides
    @Singleton
    internal fun provideContentResolver(@ForApplication context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    internal fun providePicasso(@ForApplication context: Context): Picasso {
        return Picasso.Builder(context)
            .indicatorsEnabled(BuildConfig.DEBUG)
            .loggingEnabled(BuildConfig.DEBUG)
            .build()
    }

    @Provides
    @Singleton
    internal fun providePreferencesProvider(@ForApplication context: Context): PreferencesProvider {
        return PreferencesProvider(context)
    }
}
