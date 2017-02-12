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
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import quickbeer.android.injections.ForApplication;
import quickbeer.android.instrumentation.NetworkInstrumentation;
import quickbeer.android.network.utils.DateDeserializer;
import quickbeer.android.network.utils.StringDeserializer;

@Module
public final class NetworkModule {

    @Provides
    @Singleton
    public static NetworkApi provideNetworkApi(
            @NonNull final OkHttpClient client,
            @NonNull final Gson gson) {
        return new NetworkApi(client, gson);
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(
            @NonNull final NetworkInstrumentation networkInstrumentation,
            @NonNull final ClearableCookieJar cookieJar) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .followRedirects(false)
                .followSslRedirects(false);

        return networkInstrumentation.decorateNetwork(builder).build();
    }

    @Provides
    @Singleton
    public static Gson provideUnescapingGson() {
        return new GsonBuilder()
                .registerTypeAdapter(String.class, new StringDeserializer())
                .registerTypeAdapter(DateTime.class, new DateDeserializer())
                .registerTypeAdapterFactory(RateBeerTypeAdapterFactory.create())
                .create();
    }

    @Provides
    @Singleton
    public static ClearableCookieJar provideCookieJar(@ForApplication @NonNull final Context context) {
        return new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }
}
