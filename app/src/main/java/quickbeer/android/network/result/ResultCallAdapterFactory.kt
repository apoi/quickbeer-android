package quickbeer.android.network.result

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit

class ResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): ResultAdapter? {
        val responseType = getParameterUpperBound(0, returnType as ParameterizedType)
        val resultType = getParameterUpperBound(0, responseType as ParameterizedType)

        return if (getRawType(returnType) == Call::class.java &&
            getRawType(responseType) == ApiResult::class.java
        ) {
            ResultAdapter(resultType)
        } else {
            null
        }
    }
}
