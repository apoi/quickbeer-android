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

package quickbeer.android.feature.barcode.detection

import androidx.annotation.MainThread
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import quickbeer.android.feature.barcode.BarcodeScannerViewModel
import quickbeer.android.feature.barcode.ScannerState
import quickbeer.android.feature.barcode.camera.CameraReticleAnimator
import quickbeer.android.feature.barcode.camera.FrameProcessorBase
import quickbeer.android.feature.barcode.camera.GraphicOverlay
import quickbeer.android.feature.barcode.utils.BarcodeValidator
import timber.log.Timber

/** A processor to run the barcode detector. */
class BarcodeProcessor(
    graphicOverlay: GraphicOverlay,
    private val viewModel: BarcodeScannerViewModel
) : FrameProcessorBase<List<Barcode>>() {

    private val scanner = BarcodeScanning.getClient()
    private val cameraReticleAnimator = CameraReticleAnimator(graphicOverlay)

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return scanner.process(image)
    }

    @MainThread
    override fun onSuccess(
        results: List<Barcode>,
        graphicOverlay: GraphicOverlay
    ) {
        if (!viewModel.isCameraLive) return

        graphicOverlay.clear()

        val barcode = results.firstOrNull(BarcodeValidator::isValidBarcode)
        if (barcode == null) {
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))
            viewModel.setScannerState(ScannerState.Detecting)
        } else {
            cameraReticleAnimator.cancel()
            viewModel.setScannerState(ScannerState.Detected(barcode))
        }

        graphicOverlay.invalidate()
    }

    override fun onFailure(e: Exception) {
        Timber.e(e, "Barcode detection failed!")
    }

    override fun stop() {
        super.stop()

        try {
            scanner.close()
        } catch (e: IOException) {
            Timber.e(e, "Failed to close barcode detector!")
        }
    }
}
