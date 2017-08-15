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
import android.support.annotation.Nullable;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import polanski.option.Option;
import timber.log.Timber;

import static quickbeer.android.utils.StringUtils.emptyAsNone;
import static quickbeer.android.utils.StringUtils.value;

public class LoginRedirectInterceptor implements Interceptor {

    private static final String SIGN_IN_PAGE = "/Signin_r.asp";

    private static final int HTTP_OK = 200;

    private static final int HTTP_FORBIDDEN = 403;

    private static final int HTTP_UNKNOWN_RESPONSE = 520;

    @NonNull
    private final JsonParser parser = new JsonParser();

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        MediaType contentType = Option.ofObj(response.body())
                .map(ResponseBody::contentType)
                .orDefault(() -> null);

        Option<String> body = Option.ofObj(response.body())
                .flatMap(value -> emptyAsNone(value::string));

        return isLoginRequest(request)
                ? handleLoginResponse(request, response, contentType, body)
                : validateResponse(request, response, contentType, body);
    }

    private Response handleLoginResponse(@NonNull Request request,
                                         @NonNull Response response,
                                         @Nullable MediaType contentType,
                                         @NonNull Option<String> body) {
        if (isSuccessfulLogin(request, response)) {
            Timber.d("Modifying response for successful login");

            return createResponse(request, response, contentType,
                    body.orDefault(() -> ""),
                    HTTP_OK);
        }

        if (isKnownLoginFailure(body)) {
            Timber.d("Interpreting response as failed login");

            return createResponse(request, response, contentType,
                    body.orDefault(() -> ""),
                    HTTP_FORBIDDEN);
        }

        return validateResponse(request, response, contentType, body);
    }

    @NonNull
    private Response validateResponse(@NonNull Request request,
                                      @NonNull Response response,
                                      @Nullable MediaType contentType,
                                      @NonNull Option<String> body) {
        if (!isValidJson(body)) {
            Timber.d("Response isn't JSON!");

            return createResponse(request, response, contentType,
                    body.orDefault(() -> ""),
                    HTTP_UNKNOWN_RESPONSE);
        }

        return createResponse(request, response, contentType,
                body.orDefault(() -> ""),
                response.code());
    }

    private static boolean isLoginRequest(@NonNull Request request) {
        return SIGN_IN_PAGE.equals(request.url().encodedPath());
    }

    private static boolean isSuccessfulLogin(@NonNull Request request, @NonNull Response response) {
        // Success with id in a cookie header
        if (request.url().toString().contains("Signin_r.asp")
                && response.code() == HTTP_OK
                && LoginUtils.idFromStringList(response.headers("set-cookie")).isSome()) {
            return true;
        }

        // Old-style API redirect response
        return response.isRedirect() && value(response.header("location")).contains("uid");
    }

    private static boolean isKnownLoginFailure(@NonNull Option<String> body) {
        return body.filter(value -> value.contains("failed login"))
                .isSome();
    }

    private boolean isValidJson(@NonNull Option<String> body) {
        return body.flatMap(value -> Option.tryAsOption(() -> parser.parse(value)))
                .isSome();
    }

    /**
     * Body contents can be read only once from the response, so we must create
     * a new response object; otherwise attempts to access the body contents
     * later in the chain would throw.
     */
    @NonNull
    private static Response createResponse(@NonNull Request request,
                                           @NonNull Response response,
                                           @Nullable MediaType contentType,
                                           @NonNull String body,
                                           int code) {
        return new Response.Builder()
                .request(request)
                .protocol(response.protocol())
                .code(code)
                .message(response.message())
                .handshake(response.handshake())
                .headers(response.headers())
                .body(ResponseBody.create(contentType, body))
                .networkResponse(response.networkResponse())
                .build();
    }
}
