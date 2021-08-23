package quickbeer.android.domain.login

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import quickbeer.android.data.state.State
import quickbeer.android.domain.preferences.IntPreferenceStore
import quickbeer.android.domain.preferences.StringPreferenceStore
import quickbeer.android.network.HttpCode
import quickbeer.android.network.result.ApiResult

class LoginManager @Inject constructor(
    private val loginFetcher: LoginFetcher,
    private val cookieJar: ClearableCookieJar,
    private val stringPreferenceStore: StringPreferenceStore,
    private val intPreferenceStore: IntPreferenceStore
) {

    val isLoggedIn: Flow<Boolean> = intPreferenceStore.getKeysStream()
        .map { it.contains(USERID) }
        .distinctUntilChanged()

    val userId: Flow<Int?> = intPreferenceStore.getKeysStream()
        .map { intPreferenceStore.get(USERID) }
        .distinctUntilChanged()

    suspend fun login(username: String, password: String): State<Boolean> {
        logout()

        return when (val result = loginFetcher.fetch(username, password)) {
            is ApiResult.Success -> handleSuccess(result.value, username, password)
            is ApiResult.HttpError -> handleHttpError(result)
            is ApiResult.NetworkError -> State.Error(LoginError.ConnectionError)
            is ApiResult.UnknownError -> State.Error(LoginError.UnknownError)
        }
    }

    suspend fun logout() {
        intPreferenceStore.delete(USERID)
        stringPreferenceStore.delete(USERNAME)
        stringPreferenceStore.delete(PASSWORD)
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

        intPreferenceStore.put(USERID, userId)
        stringPreferenceStore.put(USERNAME, username)
        stringPreferenceStore.put(PASSWORD, password)

        return State.Success(true)
    }

    private fun handleHttpError(result: ApiResult.HttpError): State<Boolean> {
        return when (result.code) {
            HttpCode.FORBIDDEN -> State.Error(LoginError.InvalidCredentials)
            else -> State.Error(LoginError.UnknownError)
        }
    }

    companion object {
        const val USERID = "PREF_USERID"
        const val USERNAME = "PREF_USERNAME"
        const val PASSWORD = "PREF_PASSWORD"
    }
}
