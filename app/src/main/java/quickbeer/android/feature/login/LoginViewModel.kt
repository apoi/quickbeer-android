package quickbeer.android.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.login.LoginFetcher
import quickbeer.android.domain.login.LoginResult
import quickbeer.android.network.interceptor.LoginUtils
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.Preferences

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginFetcher: LoginFetcher,
    private val cookieJar: ClearableCookieJar,
    private val preferences: Preferences
) : ViewModel() {

    private val _loginState = MutableStateFlow<State<Boolean>>(State.Initial)
    val loginState: Flow<State<Boolean>> = _loginState

    fun login(username: String, password: String) {
        logout()

        viewModelScope.launch(Dispatchers.IO) {
            _loginState.emit(State.Loading())

            val state = when (val result = loginFetcher.fetch(username, password)) {
                is ApiResult.Success -> {
                    preferences.username = username
                    preferences.password = password
                    State.Success(result.value is LoginResult.Success)
                }
                is ApiResult.HttpError -> {
                    if (result.code == LoginUtils.HTTP_FORBIDDEN) {
                        State.Error(LoginError.InvalidCredentials)
                    } else State.Error(LoginError.UnknownError)
                }
                is ApiResult.NetworkError -> State.Error(LoginError.ConnectionError)
                is ApiResult.UnknownError -> State.Error(LoginError.UnknownError)
            }

            _loginState.emit(state)
        }
    }

    fun logout() {
        preferences.username = null
        preferences.password = null
        cookieJar.clear()
    }
}
