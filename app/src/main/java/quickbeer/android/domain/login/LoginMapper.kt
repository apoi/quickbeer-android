package quickbeer.android.domain.login

import javax.inject.Inject
import okhttp3.CookieJar
import okhttp3.ResponseBody
import quickbeer.android.network.interceptor.LoginUtils
import quickbeer.android.util.Mapper

class LoginMapper @Inject constructor(
    private val cookieJar: CookieJar
) : Mapper<LoginResult, ResponseBody> {

    override fun mapFrom(source: LoginResult): ResponseBody {
        error("Not implemented")
    }

    override fun mapTo(source: ResponseBody): LoginResult {
        return LoginUtils.getUserId(cookieJar)
            ?.let(LoginResult::Success)
            ?: LoginResult.Error(LoginError())
    }
}

class LoginError : Throwable("Login failed")
