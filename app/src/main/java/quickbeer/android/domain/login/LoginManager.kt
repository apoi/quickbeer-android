package quickbeer.android.domain.login

import javax.inject.Inject
import quickbeer.android.data.state.State
import quickbeer.android.domain.beerlist.store.TickedBeersStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.HttpCode
import quickbeer.android.network.result.ApiResult

class LoginManager @Inject constructor(
    private val loginFetcher: LoginFetcher,
    private val cookieJar: LoginCookieJar,
    private val userStore: UserStore,
    private val tickedBeersStore: TickedBeersStore
) {

    suspend fun autoLogin(): ApiResult<Int> {
        val user = userStore.getCurrentUser()

        if (user?.username == null || user.password == null) {
            return ApiResult.UnknownError(IllegalStateException("Username or password missing"))
        }

        val result = loginFetcher.fetch(user.username, user.password)
        when (result) {
            is ApiResult.Success -> handleSuccess(result.value, user.username, user.password)
            is ApiResult.HttpError -> handleHttpError(result)
            is ApiResult.NetworkError -> Unit
            is ApiResult.UnknownError -> Unit
        }

        return result
    }

    suspend fun login(username: String, password: String): State<Boolean> {
        clearCookies()

        return when (val result = loginFetcher.fetch(username, password)) {
            is ApiResult.Success -> handleSuccess(result.value, username, password)
            is ApiResult.HttpError -> handleHttpError(result)
            is ApiResult.NetworkError -> State.Error(LoginError.ConnectionError)
            is ApiResult.UnknownError -> State.Error(LoginError.UnknownError)
        }
    }

    suspend fun logout() {
        val user = userStore.getCurrentUser()
        if (user?.id != null) {
            tickedBeersStore.delete(user.id.toString())
            userStore.delete(user.id)
        }

        tickedBeersStore.clearTicks()

        clearCookies()
    }

    private fun clearCookies() {
        cookieJar.clear()
    }

    private suspend fun handleSuccess(
        userId: Int?,
        username: String,
        password: String
    ): State<Boolean> {
        if (userId == null) {
            return State.Error(LoginError.UnknownError)
        }

        val user = User.createCurrentUser(userId, username, password)
        userStore.put(userId, user)

        return State.Success(true)
    }

    private fun handleHttpError(result: ApiResult.HttpError): State<Boolean> {
        return when (result.code) {
            HttpCode.FORBIDDEN -> State.Error(LoginError.InvalidCredentials)
            else -> State.Error(LoginError.UnknownError)
        }
    }
}
