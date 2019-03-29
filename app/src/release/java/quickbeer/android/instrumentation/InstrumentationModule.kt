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

import java.util.Collections

import javax.inject.Named
import javax.inject.Singleton

import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import quickbeer.android.utils.ProductionTree
import timber.log.Timber

@Module
object InstrumentationModule {

    @Provides
    @Singleton
    internal fun provideLoggingTree(): Timber.Tree {
        return ProductionTree()
    }

    @Provides
    @Singleton
    fun provideApplicationInstrumentation(): ApplicationInstrumentation {
        return NullApplicationInstrumentation()
    }

    @Provides
    @Singleton
    @Named("networkInstrumentation")
    fun provideNetworkInstrumentation(): Instrumentation {
        return NullInstrumentation()
    }

    @Provides
    @Singleton
    @Named("networkInterceptors")
    fun provideNetworkInterceptors(): List<Interceptor> {
        return Collections.emptyList()
    }
}
