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
    preferences: Preferences
) : ViewModel() {

    private val _userState = MutableStateFlow<State<User>>(State.Initial)
    val userState: Flow<State<User>> = _userState

    init {
        val userId = preferences.userId

        viewModelScope.launch(Dispatchers.IO) {
            if (userId == null) {
                _userState.emit(State.Empty)
            } else {
                userRepository.getStream(userId, User.RateCountValidator())
                    .collectLatest(_userState::emit)
            }
        }
    }
}
