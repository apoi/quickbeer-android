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
package quickbeer.android

import androidx.multidex.MultiDexApplication
import com.jakewharton.threetenabp.AndroidThreeTen
import quickbeer.android.injections.ApplicationModule
import quickbeer.android.injections.DaggerGraph
import quickbeer.android.injections.Graph
import quickbeer.android.instrumentation.ApplicationInstrumentation
import timber.log.Timber
import javax.inject.Inject

class QuickBeer : MultiDexApplication() {

    private var graph: Graph? = null

    @Inject
    internal lateinit var loggingTree: Timber.Tree

    @Inject
    internal lateinit var instrumentation: ApplicationInstrumentation

    override fun onCreate() {
        super.onCreate()

        inject()

        initLogging()

        initInstrumentation()

        initDateAndTime()
    }

    fun graph(): Graph = graph!!

    private fun inject() {
        if (graph == null) {
            graph = DaggerGraph.builder()
                .applicationModule(ApplicationModule(this))
                .build()
                .also { it.inject(this) }
        }
    }

    private fun initLogging() {
        Timber.uprootAll()
        Timber.plant(loggingTree)
    }

    private fun initInstrumentation() {
        instrumentation.init()
    }

    private fun initDateAndTime() {
        AndroidThreeTen.init(this)
    }
}
