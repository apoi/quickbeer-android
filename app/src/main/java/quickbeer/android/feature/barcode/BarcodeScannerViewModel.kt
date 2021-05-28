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
import com.google.mlkit.vision.barcode.Barcode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository

class BarcodeScannerViewModel(
    private val beerSearchRepository: BeerSearchRepository
) : ViewModel() {

    private val detectedBarcode = MutableSharedFlow<Barcode>()

    private val _scannerState = MutableStateFlow<ScannerState>(ScannerState.NotStarted)
    val scannerState: Flow<ScannerState> = _scannerState

    var isCameraLive = false
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            detectedBarcode
                .flatMapLatest { barcode ->
                    beerSearchRepository.getStream(barcode.rawValue, Accept())
                        .mapNotNull { state -> mapResult(barcode, state) }
                }
                .collectLatest(_scannerState::emit)
        }
    }

    fun setScannerState(state: ScannerState) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_scannerState.value != state) {
                _scannerState.emit(state)
            }

            if (state is ScannerState.Detected) {
                detectedBarcode.emit(state.barcode)
            }
        }
    }

    fun markCameraLive() {
        isCameraLive = true
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }

    private fun mapResult(barcode: Barcode, state: State<List<Beer>>): ScannerState? {
        return when (state) {
            is State.Loading -> ScannerState.Searching(barcode)
            is State.Success -> ScannerState.Found(barcode, state.value)
            is State.Empty -> ScannerState.NotFound(barcode)
            is State.Error -> ScannerState.Error(barcode)
        }
    }
}
