package quickbeer.android.inject

import android.content.Context
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import quickbeer.android.domain.login.LoginFetcher
import quickbeer.android.domain.login.LoginMapper
import quickbeer.android.network.NetworkConfig
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.adapter.EscapedStringAdapter
import quickbeer.android.network.adapter.ZonedDateTimeAdapter
import quickbeer.android.network.cookie.SessionPersistingCookieJar
import quickbeer.android.network.interceptor.AppKeyInterceptor
import quickbeer.android.network.interceptor.LoggingInterceptor
import quickbeer.android.network.interceptor.LoginRedirectInterceptor
import quickbeer.android.network.result.ResultCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TEN_MEGABYTES: Long = 10 * 1024 * 1024

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttp(
        @ApplicationContext context: Context,
        cookieJar: CookieJar
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, TEN_MEGABYTES))
            .cookieJar(cookieJar)
            .addInterceptor(AppKeyInterceptor())
            .addInterceptor(LoggingInterceptor.create())
            .addInterceptor(LoginRedirectInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(ZonedDateTimeAdapter())
            .add(EscapedStringAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(NetworkConfig.API_ENDPOINT)
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
    fun provideCookieJar(@ApplicationContext context: Context): CookieJar {
        return SessionPersistingCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    }

    @Provides
    @Singleton
    fun loginFetcher(rateBeerApi: RateBeerApi, cookieJar: CookieJar): LoginFetcher {
        return LoginFetcher(rateBeerApi, LoginMapper(cookieJar))
    }
}
