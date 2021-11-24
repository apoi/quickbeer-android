package quickbeer.android.data.fetcher

import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import quickbeer.android.network.result.ApiResult
import quickbeer.android.network.result.map
import quickbeer.android.util.JsonMapper

/**
 * Fetcher fetches a remote value for a specific type. Deduplicates calls so that all new
 * invocations made during an existing fetch operation will suspend and wait for the result of
 * the ongoing call.
 *
 * @param <K> Type of keys.
 * @param <V> Type of domain values.
 * @param <J> Type of result data, mostly Json objects.
 */
open class Fetcher<in K : Any, out V : Any, J : Any>(
    private val jsonMapper: JsonMapper<K, V, J>,
    private val api: suspend (K) -> ApiResult<J>
) {

    // Map of suspended fetcher invocations waiting for result
    private val map = ConcurrentHashMap<K, List<Continuation<ApiResult<V>>>>()

    // Lock for guaranteeing correct map modification order
    private val lock = ReentrantLock()

    open suspend fun fetch(key: K): ApiResult<V> {
        lock.lock()

        val continuations = map[key]

        return if (continuations == null) {
            // No requests yet, invoke the API now. Mark ongoing operation with an empty
            // list so that any other invocations will take the suspending branch.
            map[key] = emptyList()
            lock.unlock()
            fetchAndResume(key)
        } else {
            // Request is already ongoing. Suspend and add a listener for the result.
            try {
                suspendCancellableCoroutine { continuation ->
                    map[key] = continuations + continuation
                    lock.unlock()
                }
            } catch (e: CancellationException) {
                // Previous job was cancelled, this continuation was awaken to retry
                fetchAndResume(key)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun fetchAndResume(key: K): ApiResult<V> {
        val result = try {
            api.invoke(key).map(key, jsonMapper)
        } catch (error: Throwable) {
            ApiResult.mapError(error)
        }

        lock.withLock {
            if (result is ApiResult.UnknownError && result.cause is CancellationException) {
                // Cancelled without result, wake up next continuation to retry
                wakeNextContinuation(key, result)
            } else {
                // Valid result, notify all suspended listeners
                notifyContinuations(key, result)
            }
        }

        return result
    }

    private fun wakeNextContinuation(key: K, result: ApiResult.UnknownError) {
        val continuations = map.remove(key)
        val next = continuations?.firstOrNull()
        if (next != null && continuations.size > 1) {
            map[key] = continuations - next
        }
        next?.resumeWithException(result.cause)
    }

    private fun notifyContinuations(key: K, result: ApiResult<V>) {
        map.remove(key)?.forEach {
            it.resumeWith(Result.success(result))
        }
    }
}

/**
 * Fetcher that has no key for the Api.
 */
open class SingleFetcher<out V : Any, J : Any>(
    jsonMapper: JsonMapper<Unit, V, J>,
    private val api: suspend () -> ApiResult<J>
) {

    private val fetcher = Fetcher(jsonMapper) { api.invoke() }

    suspend fun fetch(): ApiResult<V> {
        return fetcher.fetch(Unit)
    }
}
