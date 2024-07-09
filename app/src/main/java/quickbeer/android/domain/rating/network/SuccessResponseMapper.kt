package quickbeer.android.domain.rating.network

import okhttp3.ResponseBody
import quickbeer.android.util.JsonMapper

class SuccessResponseMapper<T> : JsonMapper<T, Boolean, ResponseBody> {

    override fun map(key: T, source: ResponseBody): Boolean {
        return true
    }
}
