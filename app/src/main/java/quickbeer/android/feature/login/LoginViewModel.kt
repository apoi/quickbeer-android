package quickbeer.android.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.login.LoginFetcher
import quickbeer.android.domain.login.LoginResult
import quickbeer.android.network.result.ApiResult

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginFetcher: LoginFetcher
) : ViewModel() {

    private val _loginState = MutableStateFlow<State<Boolean>>(State.Initial)
    val loginState: Flow<State<Boolean>> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.emit(State.Loading())

            val state = when (val result = loginFetcher.fetch(username, password)) {
                is ApiResult.Success -> State.Success(result.value is LoginResult.Success)
                is ApiResult.HttpError -> State.Error(result.cause)
                is ApiResult.NetworkError -> State.Error(result.cause)
                is ApiResult.UnknownError -> State.Error(result.cause)
            }

            _loginState.emit(state)
        }
    }
}
