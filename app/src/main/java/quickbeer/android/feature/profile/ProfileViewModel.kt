package quickbeer.android.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.threeten.bp.Duration.ofDays
import quickbeer.android.data.repository.AlwaysFetch
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.repository.NotOlderThan
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beerlist.repository.TickedBeersRepository
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.ratinglist.repository.UserAllRatingsRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.util.ktx.isOlderThan
import quickbeer.android.util.ktx.mapState
import timber.log.Timber

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModel @Inject constructor(
    private val loginManager: LoginManager,
    private val currentUserRepository: CurrentUserRepository,
    private val tickedBeersRepository: TickedBeersRepository,
    private val userAllRatingsRepository: UserAllRatingsRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<State<User>>(State.Initial)
    val userState: StateFlow<State<User>> = _userState

    private val _ratingCountState = MutableStateFlow<State<Int>>(State.Initial)
    val ratingCountState: StateFlow<State<Int>> = _ratingCountState

    private val _tickCountState = MutableStateFlow<State<Int>>(State.Initial)
    val tickCountState: StateFlow<State<Int>> = _tickCountState

    init {
        // Current user state for display purposes, do not update anything here
        viewModelScope.launch(Dispatchers.IO) {
            currentUserRepository.getStream(NoFetch())
                .distinctUntilChanged()
                .collectLatest(_userState::emit)
        }

        // Tick data for display. Similarly should not update
        viewModelScope.launch(Dispatchers.IO) {
            userAllRatingsRepository.getStream(NoFetch())
                .mapState(List<Rating>::size)
                .distinctUntilChanged()
                .collectLatest(_ratingCountState::emit)
        }

        // Rating data for display. Again, no updating here
        viewModelScope.launch(Dispatchers.IO) {
            tickedBeersRepository.getStream(NoFetch())
                .mapState(List<Beer>::size)
                .distinctUntilChanged()
                .collectLatest(_tickCountState::emit)
        }

        // User details, tick and rating data refresh once a week, according to the timestamp
        // stored in the currently logged in User object. The ratings count in the user object
        // does not match (!!) actual tick/rating data, so this acts as a periodical full refresh
        // to get up-to-date data.
        viewModelScope.launch(Dispatchers.IO) {
            currentUserRepository.getStream(NoFetch())
                // Only proceed when we receive User
                .mapNotNull { it.valueOrNull() }
                // In case same object gets emitted multiple times
                .distinctUntilChanged()
                .onEach { Timber.d("Current user updated: ${it.updated}") }
                // Only proceed if update date doesn't exist, or is older than 7 days
                .filter { it.updated?.isOlderThan(ofDays(7)) != false }
                .flatMapLatest {
                    Timber.d("Fetch user, ticks, ratings")
                    val u = currentUserRepository.getStream(NotOlderThan(ofDays(7)) { it?.updated })
                    val t = tickedBeersRepository.getStream(AlwaysFetch())
                    val r = userAllRatingsRepository.getStream(AlwaysFetch())

                    combine(u, t, r) { newUser, newTicks, newRatings ->
                        Timber.d("Updated user: ${newUser.valueOrNull()?.updated}")
                        Timber.d("Updated ticks: ${newTicks.valueOrNull()?.size}")
                        Timber.d("Updated ratings: ${newRatings.valueOrNull()?.size}")
                    }
                }
                .collect()
        }
    }

    suspend fun logout() {
        loginManager.logout()
    }
}
