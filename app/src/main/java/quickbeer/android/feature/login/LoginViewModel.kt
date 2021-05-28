package quickbeer.android.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.network.RateBeerApi
import timber.log.Timber

class LoginViewModel(
    private val api: RateBeerApi
) : ViewModel() {

    private val _loginState = MutableStateFlow<State<Boolean>>(State.Empty)
    val loginState: Flow<State<Boolean>> = _loginState

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = api.login(username, password, "on")
            Timber.w("LOGIN RESULT: $result")
        }
    }
}
