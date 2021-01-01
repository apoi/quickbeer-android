package quickbeer.android.util

import timber.log.Timber

class Timer(private val name: String = "Timer") {

    private var startTime = System.currentTimeMillis()

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        Timber.w("$name: ${System.currentTimeMillis() - startTime} ms")
    }
}
