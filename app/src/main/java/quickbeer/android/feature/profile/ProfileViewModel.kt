package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beerlist.repository.TickedBeersRepository
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.UserRepository

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tickedBeersRepository: TickedBeersRepository,
    private val loginManager: LoginManager,
) : ViewModel() {

    val hasUser: Flow<Boolean> = loginManager.userId
        .map { it != null }

    val userState: Flow<State<User>> = loginManager.userId
        .map { it ?: throw NoUserError() }
        .flatMapLatest { userRepository.getStream(it, UserRepository.RateCountValidator()) }
        .onStart { emit(State.Initial) }
        .catch {
            if (it is NoUserError) emit(State.Empty)
            else emit(State.Error(it))
        }

    init {
        // Fetch ticks for the logged-in user if not stored yet
        viewModelScope.launch(Dispatchers.IO) {
            loginManager.userId
                .filterNotNull()
                .flatMapLatest { tickedBeersRepository.getStream(it.toString(), Accept()) }
                .collect()
        }
    }

    suspend fun logout() {
        loginManager.logout()
    }
}

private class NoUserError : Throwable()
