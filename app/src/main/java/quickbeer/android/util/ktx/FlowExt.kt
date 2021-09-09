package quickbeer.android.util.ktx

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State

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

/**
 * Doesn't emit new Success state if the enclosed list of items has the same set of IDs.
 * This avoids a new emit in case when property of one of the objects changes.
 */
fun <T> Flow<State<List<T>>>.distinctUntilNewId(getId: (T) -> Int): Flow<State<List<T>>> {
    return distinctUntilChanged { old, new ->
        old is State.Success && new is State.Success &&
            old.value.map(getId).toSet() == new.value.map(getId).toSet()
    }
}
