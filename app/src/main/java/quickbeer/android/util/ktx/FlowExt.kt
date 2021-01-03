package quickbeer.android.util.ktx

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

fun <T> Flow<T>.takeUntil(signal: Flow<*>): Flow<T> = flow {
    try {
        coroutineScope {
            launch {
                signal.take(1).collect()
                this@coroutineScope.cancel()
            }

            collect { emit(it) }
        }
    } catch (_: CancellationException) {
        // Don't emit, cancel only happens when new key was received
    }
}
