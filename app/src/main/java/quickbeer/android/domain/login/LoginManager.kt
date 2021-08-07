package quickbeer.android.domain.login

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import javax.inject.Inject
import quickbeer.android.data.state.State
import quickbeer.android.network.HttpCode
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.Preferences

class LoginManager @Inject constructor(
    private val loginFetcher: LoginFetcher,
    private val cookieJar: ClearableCookieJar,
    private val preferences: Preferences
) {

    suspend fun login(username: String, password: String): State<Boolean> {
        logout()

        return when (val result = loginFetcher.fetch(username, password)) {
            is ApiResult.Success -> handleSuccess(result.value, username, password)
            is ApiResult.HttpError -> handleHttpError(result)
            is ApiResult.NetworkError -> State.Error(LoginError.ConnectionError)
            is ApiResult.UnknownError -> State.Error(LoginError.UnknownError)
        }
    }

    fun logout() {
        preferences.username = null
        preferences.password = null
        cookieJar.clear()
    }

    private fun handleSuccess(
        userId: Int?,
        username: String,
        password: String
    ): State<Boolean> {
        if (userId == null) {
            return State.Error(LoginError.UnknownError)
        }

        preferences.username = username
        preferences.password = password
        return State.Success(true)
    }

    private fun handleHttpError(result: ApiResult.HttpError): State<Boolean> {
        return when (result.code) {
            HttpCode.FORBIDDEN -> State.Error(LoginError.InvalidCredentials)
            else -> State.Error(LoginError.UnknownError)
        }
    }
}
