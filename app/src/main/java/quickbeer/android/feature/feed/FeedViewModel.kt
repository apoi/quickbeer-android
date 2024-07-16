package quickbeer.android.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.ui.adapter.feed.FeedListModel

@HiltViewModel
class FeedViewModel @Inject constructor() : ViewModel() {

    private val _feedListState = MutableStateFlow<State<List<FeedListModel>>>(State.Initial)
    val feedListState: StateFlow<State<List<FeedListModel>>> = _feedListState

    init {
        viewModelScope.launch(Dispatchers.IO) {
        }
    }
}
