package quickbeer.android.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.login.LoginManager

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginManager: LoginManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<State<Boolean>>(State.Initial)
    val loginState: StateFlow<State<Boolean>> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.emit(State.Loading())
            _loginState.emit(loginManager.login(username, password))
        }
    }
}
