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
package quickbeer.android.network.utils;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import timber.log.Timber;

import static quickbeer.android.utils.StringUtils.value;

public class LoginRedirectInterceptor implements Interceptor {

    private static final String SIGN_IN_PAGE = "/Signin_r.asp";

    private static final int HTTP_OK = 200;

    private static final int HTTP_FORBIDDEN = 403;

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (isLoginRequest(request)) {
            return handleLoginResponse(request, response);
        }

        return response;
    }

    private static boolean isLoginRequest(@NonNull Request request) {
        return Objects.equals(request.url().encodedPath(), SIGN_IN_PAGE);
    }

    private static Response handleLoginResponse(@NonNull Request request, @NonNull Response response) {
        if (isSuccessfulLogin(request, response)) {
            Timber.d("Modifying response for successful login");

            return new Response.Builder()
                    .request(request)
                    .protocol(response.protocol())
                    .code(HTTP_OK)
                    .message(response.message())
                    .handshake(response.handshake())
                    .headers(response.headers())
                    .body(response.body())
                    .networkResponse(response.networkResponse())
                    .build();
        }

        if (isKnownLoginFailure(response)) {
            Timber.d("Interpreting response as failed login");

            return new Response.Builder()
                    .request(request)
                    .protocol(response.protocol())
                    .code(HTTP_FORBIDDEN)
                    .message(response.message())
                    .handshake(response.handshake())
                    .headers(response.headers())
                    .body(response.body())
                    .networkResponse(response.networkResponse())
                    .build();
        }

        return response;
    }

    private static boolean isSuccessfulLogin(@NonNull Request request, @NonNull Response response) {
        // Just a straightforward success
        if (request.url().toString().contains("Signin_r.asp") && response.code() == HTTP_OK) {
            return true;
        }

        // Old-style API redirect response
        return response.isRedirect() && value(response.header("location")).contains("uid");
    }

    private static boolean isKnownLoginFailure(@NonNull Response response) {
        try {
            ResponseBody body = response.body();
            return body != null && body.string().contains("failed login");
        } catch (IOException ignored) {
            return false;
        }
    }
}
