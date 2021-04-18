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

import android.Manifest.permission.CAMERA
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Camera
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.IOException
import org.koin.androidx.viewmodel.ext.android.viewModel
import quickbeer.android.R
import quickbeer.android.databinding.BarcodeScannerActivityBinding
import quickbeer.android.domain.beer.Beer
import quickbeer.android.feature.barcode.camera.CameraSource
import quickbeer.android.feature.barcode.detection.BarcodeProcessor
import quickbeer.android.feature.barcode.utils.BarcodeValidator
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

        binding.graphicOverlay.setOnClickListener { startCamera() }
        binding.flashButton.setOnClickListener { onFlashPressed() }
        setSupportActionBar(binding.toolbar)
        observeScannerState()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(this, CAMERA) != PERMISSION_GRANTED) {
            requestPermission()
        } else {
            startCamera()
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), PERMISSION_RESULT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val result = grantResults.firstOrNull()
        when {
            requestCode != PERMISSION_RESULT -> finish()
            result == PERMISSION_GRANTED -> startCamera()
            result == PERMISSION_DENIED -> showRationale()
            else -> finish()
        }
    }

    private fun showRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.permission_explanation)
                .setPositiveButton(R.string.action_yes) { _, _ -> requestPermission() }
                .setNegativeButton(R.string.action_no) { _, _ -> finish() }
                .show()
        } else {
            MaterialAlertDialogBuilder(this)
                .setMessage(R.string.permission_missing)
                .setPositiveButton(R.string.action_ok) { _, _ -> finish() }
                .show()
        }
    }

    private fun startCamera() {
        viewModel.markCameraFrozen()
        cameraSource?.setFrameProcessor(BarcodeProcessor(binding.graphicOverlay, viewModel))
        viewModel.setScannerState(ScannerState.Detecting)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setScannerState(ScannerState.NotStarted)
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
        observe(viewModel.scannerState) { state ->
            when (state) {
                is ScannerState.NotStarted -> Unit
                is ScannerState.Detecting -> {
                    showPrompt(getString(R.string.prompt_point_at_a_barcode))
                    startCameraPreview()
                }
                is ScannerState.Confirming -> {
                    showPrompt(getString(R.string.prompt_move_camera_closer))
                    startCameraPreview()
                }
                is ScannerState.Detected -> {
                    showPrompt(null)
                    stopCameraPreview()
                }
                is ScannerState.Searching -> {
                    showPrompt(getString(R.string.prompt_searching))
                    stopCameraPreview()
                }
                is ScannerState.Found -> {
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(KEY_BARCODE, state.barcode.rawValue)
                        putParcelableArrayListExtra(KEY_BEERS, ArrayList(state.beers))
                    })
                    finish()
                }
                is ScannerState.NotFound -> {
                    showPrompt(getString(R.string.prompt_no_results).format(state.barcode.rawValue))
                    stopCameraPreview()
                }
                is ScannerState.Error -> {
                    showPrompt(getString(R.string.prompt_error).format(state.barcode.rawValue))
                    stopCameraPreview()
                }
            }
        }
    }

    private fun showPrompt(message: String?) {
        val promptChip = binding.bottomPromptChip
        val wasPromptChipGone = !promptChip.isVisible

        message?.let(promptChip::setText)
        promptChip.isVisible = message != null

        val shouldAnimateChip = wasPromptChipGone && promptChip.isVisible
        if (shouldAnimateChip && !promptChipAnimator.isRunning) promptChipAnimator.start()
    }

    companion object {
        const val KEY_BARCODE = "barcode"
        const val KEY_BEERS = "beers"
        const val BARCODE_RESULT = 0x200

        private const val PERMISSION_RESULT = 0x500
    }
}
