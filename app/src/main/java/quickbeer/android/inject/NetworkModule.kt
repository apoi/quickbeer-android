package quickbeer.android.inject

import android.content.Context
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.OkHttpClient
import quickbeer.android.BuildConfig
import quickbeer.android.Constants
import quickbeer.android.domain.login.LoginCookieJar
import quickbeer.android.domain.login.LoginFetcher
import quickbeer.android.domain.login.LoginMapper
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.adapter.StripHtmlStringAdapter
import quickbeer.android.network.adapter.UnescapeHtmlEntitiesStringAdapter
import quickbeer.android.network.adapter.ZonedDateTimeAdapter
import quickbeer.android.network.interceptor.AppKeyInterceptor
import quickbeer.android.network.interceptor.AuthorizationErrorInterceptor
import quickbeer.android.network.interceptor.LoggingInterceptor
import quickbeer.android.network.interceptor.LoginRedirectInterceptor
import quickbeer.android.network.result.ResultCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TEN_MEGABYTES: Long = 10 * 1024 * 1024

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RateBeerMoshi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HtmlPreservingMoshi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HtmlPreservingApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(
        @ApplicationContext context: Context,
        cookieJar: LoginCookieJar
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, TEN_MEGABYTES))
            .cookieJar(cookieJar)
            .writeTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthorizationErrorInterceptor(cookieJar))
            .addInterceptor(AppKeyInterceptor())
            .addInterceptor(LoginRedirectInterceptor())

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(LoggingInterceptor.create())
        }

        return builder.build()
    }

    @Provides
    @Reusable
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Reusable
    @RateBeerMoshi
    fun provideRateBeerMoshi(): Moshi {
        return Moshi.Builder()
            .add(ZonedDateTimeAdapter())
            .add(StripHtmlStringAdapter())
            .build()
    }

    @Provides
    @Reusable
    @HtmlPreservingMoshi
    fun provideHtmlPreservingMoshi(): Moshi {
        return Moshi.Builder()
            .add(ZonedDateTimeAdapter())
            .add(UnescapeHtmlEntitiesStringAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, @RateBeerMoshi moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Constants.API_URL)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideRateBeerApi(retrofit: Retrofit): RateBeerApi {
        return retrofit.create(RateBeerApi::class.java)
    }

    @Provides
    @Singleton
    @HtmlPreservingApi
    fun provideHtmlPreservingRateBeerApi(
        okHttpClient: OkHttpClient,
        @HtmlPreservingMoshi moshi: Moshi
    ): RateBeerApi {
        val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Constants.API_URL)
            .client(okHttpClient)
            .build()

        return retrofit.create(RateBeerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginCookieJar(@ApplicationContext context: Context): LoginCookieJar {
        return LoginCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    }

    @Provides
    @Singleton
    fun loginFetcher(rateBeerApi: RateBeerApi, cookieJar: LoginCookieJar): LoginFetcher {
        return LoginFetcher(rateBeerApi, LoginMapper(cookieJar))
    }
}
