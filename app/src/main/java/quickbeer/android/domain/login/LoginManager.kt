package quickbeer.android.domain.login

import javax.inject.Inject
import quickbeer.android.Constants
import quickbeer.android.data.state.State
import quickbeer.android.domain.beerlist.store.TickedBeersStore
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.preferences.store.StringPreferenceStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.HttpCode
import quickbeer.android.network.result.ApiResult

class LoginManager @Inject constructor(
    private val loginFetcher: LoginFetcher,
    private val cookieJar: LoginCookieJar,
    private val userStore: UserStore,
    private val tickedBeersStore: TickedBeersStore,
    private val intPreferenceStore: IntPreferenceStore,
    private val stringPreferenceStore: StringPreferenceStore
) {

    suspend fun autoLogin(): ApiResult<Int> {
        val username = stringPreferenceStore.get(Constants.USERNAME)
        val password = stringPreferenceStore.get(Constants.PASSWORD)

        if (username == null || password == null) {
            return ApiResult.UnknownError(IllegalStateException("Username or password missing"))
        }

        val result = loginFetcher.fetch(username, password)
        when (result) {
            is ApiResult.Success -> handleSuccess(result.value, username, password)
            is ApiResult.HttpError -> handleHttpError(result)
            is ApiResult.NetworkError -> Unit
            is ApiResult.UnknownError -> Unit
        }

        return result
    }

    suspend fun login(username: String, password: String): State<Boolean> {
        clearLogin()

        return when (val result = loginFetcher.fetch(username, password)) {
            is ApiResult.Success -> handleSuccess(result.value, username, password)
            is ApiResult.HttpError -> handleHttpError(result)
            is ApiResult.NetworkError -> State.Error(LoginError.ConnectionError)
            is ApiResult.UnknownError -> State.Error(LoginError.UnknownError)
        }
    }

    suspend fun logout() {
        val id = intPreferenceStore.get(Constants.USERID)

        clearLogin()

        stringPreferenceStore.delete(Constants.USERNAME)
        stringPreferenceStore.delete(Constants.PASSWORD)

        if (id != null) tickedBeersStore.delete(id.toString())
        tickedBeersStore.clearTicks()
    }

    private suspend fun clearLogin() {
        intPreferenceStore.delete(Constants.USERID)
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

        intPreferenceStore.put(Constants.USERID, userId)
        stringPreferenceStore.put(Constants.USERNAME, username)
        stringPreferenceStore.put(Constants.PASSWORD, password)

        userStore.put(User(id = userId, username = username))

        return State.Success(true)
    }

    private fun handleHttpError(result: ApiResult.HttpError): State<Boolean> {
        return when (result.code) {
            HttpCode.FORBIDDEN -> State.Error(LoginError.InvalidCredentials)
            else -> State.Error(LoginError.UnknownError)
        }
    }

    companion object {
    }
}
