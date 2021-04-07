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

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.io.IOException
import org.koin.androidx.viewmodel.ext.android.viewModel
import quickbeer.android.R
import quickbeer.android.databinding.BarcodeScannerActivityBinding
import quickbeer.android.feature.barcode.BarcodeScannerViewModel.ScannerState
import quickbeer.android.feature.barcode.camera.CameraSource
import quickbeer.android.feature.barcode.detection.BarcodeProcessor
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding
import timber.log.Timber

class BarcodeScannerActivity : AppCompatActivity(R.layout.barcode_scanner_activity) {

    private val binding by viewBinding(BarcodeScannerActivityBinding::bind)
    private val viewModel by viewModel<BarcodeScannerViewModel>()

    private var cameraSource: CameraSource? = null
    private lateinit var promptChipAnimator: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val animatorSource = R.animator.bottom_prompt_chip_enter
        val animatorSet = (AnimatorInflater.loadAnimator(this, animatorSource) as AnimatorSet)
        promptChipAnimator = animatorSet.apply { setTarget(binding.bottomPromptChip) }
        cameraSource = CameraSource(binding.graphicOverlay)

        binding.closeButton.setOnClickListener { onBackPressed() }
        binding.flashButton.setOnClickListener { onFlashPressed() }

        observeScannerState()
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(this, PERMISSION_CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(PERMISSION_CAMERA), PERMISSION_RESULT)
        } else {
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_RESULT && grantResults.first() == PERMISSION_GRANTED) {
            startCamera()
        } else {
            finish()
        }
    }

    private fun startCamera() {
        viewModel.markCameraFrozen()
        cameraSource?.setFrameProcessor(BarcodeProcessor(binding.graphicOverlay, viewModel))
        viewModel.setScannerState(ScannerState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setScannerState(ScannerState.NOT_STARTED)
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    private fun onFlashPressed() {
        val selected = !binding.flashButton.isSelected
        val mode = if (selected) {
            Camera.Parameters.FLASH_MODE_TORCH
        } else Camera.Parameters.FLASH_MODE_OFF

        binding.flashButton.isSelected = selected
        cameraSource?.updateFlashMode(mode)
    }

    private fun startCameraPreview() {
        val cameraSource = cameraSource ?: return

        if (!viewModel.isCameraLive) {
            try {
                viewModel.markCameraLive()
                binding.cameraPreview.start(cameraSource)
            } catch (e: IOException) {
                Timber.e(e, "Failed to start camera preview!")
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        if (viewModel.isCameraLive) {
            viewModel.markCameraFrozen()
            binding.flashButton.isSelected = false
            binding.cameraPreview.stop()
        }
    }

    private fun observeScannerState() {
        observe(viewModel.scannerState) { workflowState ->
            val promptChip = binding.bottomPromptChip
            val wasPromptChipGone = !promptChip.isVisible

            when (workflowState) {
                ScannerState.DETECTING -> {
                    promptChip.visibility = View.VISIBLE
                    promptChip.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                ScannerState.CONFIRMING -> {
                    promptChip.visibility = View.VISIBLE
                    promptChip.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                ScannerState.SEARCHING -> {
                    promptChip.visibility = View.VISIBLE
                    promptChip.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                ScannerState.DETECTED, ScannerState.SEARCHED -> {
                    promptChip.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip.visibility = View.GONE
            }

            val shouldAnimateChip = wasPromptChipGone && promptChip.isVisible
            if (shouldAnimateChip && !promptChipAnimator.isRunning) promptChipAnimator.start()
        }

        observe(viewModel.detectedBarcode) { barcode ->
            Timber.d("Barcode result: ${barcode.rawValue}")
            setResult(RESULT_OK, Intent().apply { putExtra(BARCODE_KEY, barcode.rawValue) })
            finish()
        }
    }

    companion object {
        const val BARCODE_KEY = "barcode"
        const val BARCODE_RESULT = 0x200

        private const val PERMISSION_CAMERA = "android.permission.CAMERA"
        private const val PERMISSION_RESULT = 0x500
    }
}
