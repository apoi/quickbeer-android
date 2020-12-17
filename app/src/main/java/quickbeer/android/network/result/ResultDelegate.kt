package quickbeer.android.network.result

import java.io.IOException
import quickbeer.android.network.CallDelegate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ResultDelegate<T>(proxy: Call<T>) : CallDelegate<T, ApiResult<T>>(proxy) {

    override fun delegatedEnqueue(callback: Callback<ApiResult<T>>) = proxy.enqueue(
        object : Callback<T> {

            @Suppress("MagicNumber")
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val result = when (val code = response.code()) {
                    in 200 until 300 -> ApiResult.Success(response.body())
                    else -> ApiResult.HttpError(code, HttpException(response))
                }

                callback.onResponse(this@ResultDelegate, Response.success(result))
            }

            override fun onFailure(call: Call<T>, error: Throwable) {
                val result = when (error) {
                    is HttpException -> ApiResult.HttpError(error.code(), error)
                    is IOException -> ApiResult.NetworkError(error)
                    else -> ApiResult.UnknownError(error)
                }

                callback.onResponse(this@ResultDelegate, Response.success(result))
            }
        }
    )

    override fun delegatedClone() = ResultDelegate(proxy.clone())
}
