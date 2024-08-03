package quickbeer.android.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.R
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.ui.adapter.discover.DiscoverListModel
import quickbeer.android.ui.adapter.discover.DiscoverListModel.Link
import quickbeer.android.util.groupitem.GroupItem.Position
import quickbeer.android.util.ktx.groupItems

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    currentUserRepository: CurrentUserRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<State<List<DiscoverListModel>>>(State.Initial)
    val listState: StateFlow<State<List<DiscoverListModel>>> = _listState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserRepository.getStream(NoFetch())
                .map { it.valueOrNull()?.loggedIn == true }
                .map { isLoggedIn -> State.Success(createItemList(isLoggedIn)) }
                .collectLatest(_listState::emit)
        }
    }

    private fun createItemList(isLoggedIn: Boolean): List<DiscoverListModel> {
        return listOf(
            DiscoverListModel(
                link = Link.TOP_BEERS,
                icon = R.drawable.ic_hero_star,
                title = R.string.discover_top_beers,
                position = Position.ONLY
            ),
            DiscoverListModel(
                link = Link.ALL_COUNTRIES,
                icon = R.drawable.ic_hero_world,
                title = R.string.discover_countries,
                position = Position.FIRST
            ),
            DiscoverListModel(
                link = Link.ALL_STYLES,
                icon = R.drawable.ic_hero_beaker,
                title = R.string.discover_styles,
                position = Position.LAST
            )
        ) + listOfNotNull(
            DiscoverListModel(
                link = Link.FEED_FRIENDS,
                icon = R.drawable.ic_hero_user_group,
                title = R.string.feed_friends,
                position = Position.FIRST
            ).takeIf { isLoggedIn },
            DiscoverListModel(
                link = Link.FEED_LOCAL,
                icon = R.drawable.ic_hero_flag,
                title = R.string.feed_local,
                position = Position.MIDDLE
            ).takeIf { isLoggedIn },
            DiscoverListModel(
                link = Link.FEED_GLOBAL,
                icon = R.drawable.ic_hero_globe,
                title = R.string.feed_global,
                position = Position.LAST
            )
        ).groupItems()
    }
}
