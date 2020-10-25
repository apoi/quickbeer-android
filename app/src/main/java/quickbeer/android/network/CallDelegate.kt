package quickbeer.android.network

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class CallDelegate<T, R>(
    protected val proxy: Call<T>
) : Call<R> {

    override fun execute(): Response<R> = throw NotImplementedError()

    final override fun enqueue(callback: Callback<R>) = delegatedEnqueue(callback)

    final override fun clone(): Call<R> = delegatedClone()

    override fun cancel() = proxy.cancel()

    override fun request(): Request = proxy.request()

    override fun timeout(): Timeout = proxy.timeout()

    override fun isExecuted() = proxy.isExecuted

    override fun isCanceled() = proxy.isCanceled

    abstract fun delegatedEnqueue(callback: Callback<R>)

    abstract fun delegatedClone(): Call<R>
}
