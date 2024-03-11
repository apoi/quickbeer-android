package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.state.State
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.UserRepository

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val loginManager: LoginManager
) : ViewModel() {

    private val _hasUser = MutableStateFlow<State<Boolean>>(State.Initial)
    val hasUser: Flow<State<Boolean>> = _hasUser

    private val _userState = MutableStateFlow<State<User>>(State.Initial)
    val userState: Flow<State<User>> = _userState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            loginManager.userId
                .map { State.from(it != null) }
                .collectLatest(_hasUser::emit)
        }

        viewModelScope.launch(Dispatchers.IO) {
            loginManager.userId
                .flatMapLatest {
                    if (it != null) {
                        userRepository.getStream(it, USER_VALIDATOR)
                    } else {
                        flow { emit(State.Empty) }
                    }
                }
                .collectLatest(_userState::emit)
        }
    }

    suspend fun logout() {
        loginManager.logout()
    }

    companion object {
        private val USER_VALIDATOR = object : Validator<User> {
            override suspend fun validate(value: User?): Boolean {
                return value?.tickCount != null
            }
        }
    }
}
