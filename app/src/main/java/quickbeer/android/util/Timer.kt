package quickbeer.android.util

import timber.log.Timber

class Timer(private val name: String = "Timer") {

    private val startTime = System.currentTimeMillis()

    fun stop() {
        Timber.w("$name: ${System.currentTimeMillis() - startTime} ms")
    }
}
