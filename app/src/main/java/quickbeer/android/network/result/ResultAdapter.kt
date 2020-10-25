package quickbeer.android.network.result

import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter

class ResultAdapter(
    private val type: Type
) : CallAdapter<Type, Call<ApiResult<Type>>> {

    override fun responseType() = type

    override fun adapt(call: Call<Type>): Call<ApiResult<Type>> = ResultDelegate(call)
}
