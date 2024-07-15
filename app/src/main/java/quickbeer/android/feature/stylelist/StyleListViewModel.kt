package quickbeer.android.feature.stylelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.ui.adapter.style.StyleListModel
import quickbeer.android.util.ktx.mapState
import quickbeer.android.util.ktx.mapStateList

@HiltViewModel
class StyleListViewModel @Inject constructor(
    styleListRepository: StyleListRepository
) : ViewModel() {

    private val _styleListState = MutableStateFlow<State<List<StyleListModel>>>(State.Initial)
    val styleListState: StateFlow<State<List<StyleListModel>>> = _styleListState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            styleListRepository.getStream(Accept())
                .mapState(::filterStyles)
                .onStart { emit(State.Loading()) }
                .mapStateList(::StyleListModel)
                .collectLatest(_styleListState::emit)
        }
    }

    private fun filterStyles(styles: List<Style>): List<Style> {
        return styles
            .filter { it.parent != null && it.parent > 0 }
            .sortedBy(Style::name)
    }
}
