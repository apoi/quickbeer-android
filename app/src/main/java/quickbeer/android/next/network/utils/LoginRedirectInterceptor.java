/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.next.network.utils;

import java.io.IOException;

import io.reark.reark.utils.Log;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class LoginRedirectInterceptor implements Interceptor {
    private static final String TAG = LoginRedirectInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (request.url().encodedPath().equals("/Signin_r.asp") && response.isRedirect()) {
            Log.d(TAG, "Modifying response for login request");

            return new Response.Builder()
                    .request(request)
                    .protocol(response.protocol())
                    .code(200)
                    .message(response.message())
                    .handshake(response.handshake())
                    .headers(response.headers())
                    .body(response.body())
                    .networkResponse(response.networkResponse())
                    .build();
        }

        return response;
    }
}
