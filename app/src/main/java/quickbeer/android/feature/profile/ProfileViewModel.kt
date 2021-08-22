package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.UserRepository
import quickbeer.android.util.Preferences

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userRepository: UserRepository,
    private val preferences: Preferences
) : ViewModel() {

    var userId: Int? = preferences.userId

    private val _userState = MutableStateFlow<State<User>>(State.Initial)
    val userState: Flow<State<User>> = _userState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (userId == null) {
                _userState.emit(State.Empty)
            } else {
                userRepository.getStream(userId!!, UserRepository.RateCountValidator())
                    .collectLatest(_userState::emit)
            }
        }
    }

    fun requireUserId(): Int {
        return userId ?: error("Invalid access to user ID")
    }

    fun logout() {
        preferences.userId = null
        preferences.username = null
        preferences.password = null
    }
}
