/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quickbeer.android.feature.barcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.util.ktx.distinctUntilNewId

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val beerSearchRepository: BeerSearchRepository
) : ViewModel() {

    private val scannerState = MutableSharedFlow<ScannerState>()

    var isCameraLive = false
        private set

    fun scan(): Flow<ScannerState> {
        // Emulator testing
        // 036000291452
        // 4036328000015
        val resultFlow = scannerState
            .filterIsInstance<ScannerState.Detected>()
            .flatMapLatest { barcode ->
                beerSearchRepository.getStream(barcode.barcode, Accept())
                    .distinctUntilNewId(Beer::id)
                    .mapNotNull { state -> mapResult(barcode.barcode, state) }
            }

        return merge(scannerState, resultFlow)
            .distinctUntilChanged()
    }

    fun setScannerState(state: ScannerState) {
        viewModelScope.launch {
            scannerState.emit(state)
        }
    }

    fun markCameraLive() {
        isCameraLive = true
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }

    private fun mapResult(barcode: String, state: State<List<Beer>>): ScannerState {
        return when (state) {
            is State.Initial -> ScannerState.NotStarted
            is State.Loading -> ScannerState.Searching(barcode)
            is State.Success -> ScannerState.Found(state.value)
            is State.Empty -> ScannerState.NotFound(barcode)
            is State.Error -> ScannerState.Error(barcode)
        }
    }
}
