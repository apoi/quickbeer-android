package quickbeer.android.domain.login

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.preferences.store.StringPreferenceStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.network.HttpCode
import quickbeer.android.network.result.ApiResult

class LoginManager @Inject constructor(
    private val loginFetcher: LoginFetcher,
    private val cookieJar: ClearableCookieJar,
    private val userStore: UserStore,
    private val stringPreferenceStore: StringPreferenceStore,
    private val intPreferenceStore: IntPreferenceStore
) {

    val isLoggedIn: Flow<Boolean> = intPreferenceStore.getKeysStream()
        .map { it.contains(USERID) }
        .distinctUntilChanged()

    val userId: Flow<Int?> = intPreferenceStore.getKeysStream()
        .map { intPreferenceStore.get(USERID) }
        .distinctUntilChanged()

    private var job: Job? = null

    init {
        // User store doesn't persist, so init with locally store user details.
        // Alternatively username could be fetched from backend on demand.
        job = CoroutineScope(Dispatchers.IO).launch {
            val userId = intPreferenceStore.get(LoginManager.USERID)
            val username = stringPreferenceStore.get(LoginManager.USERNAME)

            if (userId != null) {
                userStore.put(userId, User(id = userId, username = username))
            }
        }
    }

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

        userStore.put(userId, User(id = userId, username = username))

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
