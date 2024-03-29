package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
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
        .flatMapLatest {
            if (it != null) userRepository.getStream(it, Accept())
            else flow { emit(State.Empty) }
        }
        .onStart { emit(State.Initial) }

    val tickCount: Flow<State<Int>> = loginManager.userId
        .filterNotNull()
        .flatMapLatest { tickedBeersRepository.store.getLocalTicks() }
        .map { State.from(it.size) }
        .onStart { emit(State.Loading()) }

    suspend fun logout() {
        loginManager.logout()
    }
}
