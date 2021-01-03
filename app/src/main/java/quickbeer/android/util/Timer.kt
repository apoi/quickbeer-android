package quickbeer.android.util

import timber.log.Timber

class Timer(private val name: String = "Timer") {

    private var startTime = System.currentTimeMillis()

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun stop(message: String = "") {
        val tag = name + message.takeIf(String::isNotEmpty)?.let { "/$it" }
        Timber.w("$tag: ${System.currentTimeMillis() - startTime} ms")
    }
}
