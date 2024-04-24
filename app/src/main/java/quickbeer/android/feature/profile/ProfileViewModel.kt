package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.state.State
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.CurrentUserRepository

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val currentUserRepository: CurrentUserRepository,
    private val loginManager: LoginManager
) : ViewModel() {

    private val _hasUser = MutableStateFlow<State<Boolean>>(State.Initial)
    val hasUser: Flow<State<Boolean>> = _hasUser

    private val _userState = MutableStateFlow<State<User>>(State.Initial)
    val userState: Flow<State<User>> = _userState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserRepository.getStream(NoFetch())
                .distinctUntilChanged()
                .map {
                    val user = it.valueOrNull()
                    State.from(user != null)
                }
                .collectLatest(_hasUser::emit)
        }

        viewModelScope.launch(Dispatchers.IO) {
            currentUserRepository.getStream(USER_VALIDATOR)
                .distinctUntilChanged()
                .map { State.from(it.valueOrNull()) }
                .collectLatest(_userState::emit)
        }
    }

    suspend fun logout() {
        loginManager.logout()
    }

    companion object {
        private val USER_VALIDATOR = object : Validator<User?> {
            override suspend fun validate(value: User?): Boolean {
                /**
                 * Valid states in this view:
                 *   1) User is not logged in
                 *   2) User is logged in, and tick count has been fetched
                 */
                return value == null || value.tickCount != null
            }
        }
    }
}
