/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package quickbeer.android.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.threeten.bp.ZonedDateTime;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import quickbeer.android.BuildConfig;
import quickbeer.android.Constants;
import quickbeer.android.injections.ForApplication;
import quickbeer.android.network.utils.ApiDateDeserializer;
import quickbeer.android.network.utils.ApiStringDeserializer;
import quickbeer.android.network.utils.DateDeserializer;
import quickbeer.android.network.utils.LoginRedirectInterceptor;
import quickbeer.android.network.utils.SessionPersistingCookieJar;

@Module
public final class NetworkModule {

    @Provides
    @Singleton
    public static NetworkApi provideNetworkApi(
            @NonNull OkHttpClient client,
            @NonNull ClearableCookieJar cookieJar,
            @Named("deserializingGson") @NonNull Gson gson) {
        return new NetworkApi(client, cookieJar, gson);
    }

    @Provides
    @Singleton
    @Named(Constants.API_KEY_NAME)
    static String provideApiKey() {
        return BuildConfig.RATEBEER_API_KEY;
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(
            @Named("networkInterceptors") List<Interceptor> networkInterceptors,
            @NonNull ClearableCookieJar cookieJar) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor(new LoginRedirectInterceptor());

        builder.networkInterceptors().addAll(networkInterceptors);

        return builder.build();
    }

    @Provides
    @Singleton
    public static Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new DateDeserializer())
                .create();
    }

    @Provides
    @Singleton
    @Named("deserializingGson")
    public static Gson provideDeserializingGson() {
        return new GsonBuilder()
                .registerTypeAdapter(String.class, new ApiStringDeserializer())
                .registerTypeAdapter(ZonedDateTime.class, new ApiDateDeserializer())
                .create();
    }

    @Provides
    @Singleton
    public static ClearableCookieJar provideCookieJar(@ForApplication @NonNull Context context) {
        return new SessionPersistingCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }
}
