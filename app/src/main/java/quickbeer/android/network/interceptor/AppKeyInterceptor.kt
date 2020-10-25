package quickbeer.android.network.interceptor

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import quickbeer.android.BuildConfig

class AppKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .url(createUrl(chain.request().url))
            .build()

        return chain.proceed(request)
    }

    private fun createUrl(url: HttpUrl): HttpUrl {
        return if (url.host == RATEBEER_HOST) {
            url.newBuilder()
                .addQueryParameter("k", BuildConfig.APP_KEY)
                .build()
        } else url
    }

    companion object {
        private const val RATEBEER_HOST = "ratebeer.com"
    }
}
