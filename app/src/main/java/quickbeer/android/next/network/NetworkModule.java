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
package quickbeer.android.next.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.net.CookieManager;
import java.net.CookiePolicy;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import quickbeer.android.next.injections.ForApplication;
import quickbeer.android.next.network.utils.DateDeserializer;
import quickbeer.android.next.network.utils.NetworkInstrumentation;
import quickbeer.android.next.network.utils.PersistentCookieStore;
import quickbeer.android.next.network.utils.StringDeserializer;

@Module
public final class NetworkModule {

    @Provides
    @Singleton
    public static NetworkApi provideNetworkApi(OkHttpClient client, Gson gson) {
        return new NetworkApi(client, gson);
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(
            NetworkInstrumentation<OkHttpClient.Builder> networkInstrumentation,
            CookieManager cookieManager) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
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
                .create();
    }

    @Provides
    @Singleton
    public static CookieManager provideCookieManager(@ForApplication Context context) {
        return new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }
}
