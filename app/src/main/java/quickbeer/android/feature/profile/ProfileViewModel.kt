package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

    val hasUser: Flow<Boolean> = loginManager.userId
        .map { it != null }

    val userState: Flow<State<User>> = loginManager.userId
        .flatMapLatest {
            if (it != null) {
                userRepository.getStream(it, USER_VALIDATOR)
            } else {
                flow { emit(State.Empty) }
            }
        }
        .onStart { emit(State.Initial) }

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
