package quickbeer.android.util

import timber.log.Timber

class Timer(private val name: String = "Timer") {

    private var startTime = System.currentTimeMillis()

    fun start(): Timer {
        startTime = System.currentTimeMillis()
        return this
    }

    fun stop(message: String = ""): Timer {
        val tag = name + message.takeIf(String::isNotEmpty)?.let { "/$it" }.orEmpty()
        Timber.w("$tag: ${System.currentTimeMillis() - startTime} ms")
        Timber.w("Thread " + Thread.currentThread())
        return this
    }
}
