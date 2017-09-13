/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.providers

import io.reark.reark.data.DataStreamNotification
import rx.Observable
import rx.Subscription
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class ProgressStatusProvider {

    enum class Status {
        IDLE,
        INDEFINITE,
        LOADING
    }

    private val progressSubject = BehaviorSubject.create(ProgressStatus.IDLE)

    private val progressMap = ConcurrentHashMap<Int, Float>(20, 0.75f, 4)

    private var nextId: AtomicInteger = AtomicInteger(1)

    fun addProgressObservable(notificationObservable: Observable<DataStreamNotification<*>>): Subscription {
        val progressId = createId()

        return notificationObservable
                .observeOn(Schedulers.computation())
                .filter { !it.isOnNext } // Only interested in status changes
                .map{ toProgress(it) }
                .doOnUnsubscribe { finishProgress(progressId) }
                .subscribe({ addProgress(progressId, it) }, { Timber.w(it) })
    }

    fun progressStatus(): Observable<ProgressStatus> {
        return progressSubject.asObservable()
                .onBackpressureBuffer()
                .distinctUntilChanged()
    }

    private fun createId(): Int {
        return nextId.getAndIncrement()
    }

    private fun addProgress(identifier: Int, progress: Float) {
        Timber.v("addProgress(%s, %s)", identifier, progress)

        // Skip progress update if it's going straight to finished
        if (progressMap.containsKey(identifier) || progress < 1.0) {
            progressMap.put(identifier, progress)
            aggregate()
        }
    }

    private fun finishProgress(identifier: Int) {
        Timber.v("finishProgress(%s)", identifier)

        progressMap.remove(identifier)
        aggregate()
    }

    private fun toProgress(notification: DataStreamNotification<*>): Float {
        return if (notification.isOngoing) 0.5f else 1.0f
    }

    @Synchronized
    private fun aggregate() {
        var aggregate = ProgressStatus.IDLE
        val values = progressMap.values
        val count = values.size

        if (count > 0) {
            val sum = values.sum()
            val progress: Float = if (count > 0) sum / count else 0F
            aggregate = createStatus(count, progress)
        }

        progressSubject.onNext(aggregate)

        // Status is idle when all progresses are idle, and this aggregate has finished.
        if (aggregate.status == Status.IDLE) {
            progressMap.clear()
        }
    }

    private fun createStatus(count: Int, progress: Float): ProgressStatus {
        if (count == 0) {
            // Nothing in progress, though shouldn't happen
            return ProgressStatus.IDLE
        } else if (count == 1 && progress < 1) {
            // One request in who knows what state. We don't receive progress
            // notifications, so we can't show progress for a single request.
            return ProgressStatus.INDEFINITE
        } else if (progress < 1) {
            // Multiple requests or done, we can actually show progress
            return ProgressStatus(Status.LOADING, progress)
        } else {
            // Finished, back to idle
            return ProgressStatus.IDLE
        }
    }

    data class ProgressStatus(val status: Status, val progress: Float) {
        companion object {
            val IDLE = ProgressStatus(Status.IDLE, 0.0f)
            val INDEFINITE = ProgressStatus(Status.INDEFINITE, 0.0f)
        }
    }
}
