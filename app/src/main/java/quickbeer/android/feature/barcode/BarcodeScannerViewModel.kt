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

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.Barcode

/** View model for handling scanning workflow based on camera preview.  */
class BarcodeScannerViewModel : ViewModel() {

    val scannerState = MutableLiveData(ScannerState.NOT_STARTED)
    val detectedBarcode = MutableLiveData<Barcode>()

    var isCameraLive = false
        private set

    @MainThread
    fun setScannerState(state: ScannerState) {
        if (scannerState.value != state) {
            scannerState.value = state
        }
    }

    fun markCameraLive() {
        isCameraLive = true
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }

    /**
     * State set of the scanning workflow.
     */
    enum class ScannerState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        SEARCHING,
        SEARCHED
    }
}
