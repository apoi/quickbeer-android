package quickbeer.android.feature.styles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.data.state.State.Success
import quickbeer.android.data.state.StateListMapper
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.ui.adapter.style.StyleListModel

class StylesViewModel(
    private val repository: StyleListRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<State<List<StyleListModel>>>()
    val viewState: LiveData<State<List<StyleListModel>>> = _viewState

    init {
        viewModelScope.launch {
            repository.getStream(Accept())
                .map { state ->
                    when (state) {
                        is Success -> Success(sortStyles(state.value))
                        else -> state
                    }
                }
                .map(StateListMapper(::StyleListModel)::map)
                .collect { _viewState.postValue(it) }
        }
    }

    private fun sortStyles(styles: List<Style>): List<Style> {
        return styles.filter { it.parent != null && it.parent > 0 }
            .sortedBy(Style::name)
    }
}
