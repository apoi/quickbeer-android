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
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import quickbeer.android.R
import quickbeer.android.databinding.BarcodeScannerFragmentBinding
import quickbeer.android.feature.barcode.camera.CameraSource
import quickbeer.android.feature.barcode.detection.BarcodeProcessor
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding
import timber.log.Timber

@AndroidEntryPoint
class BarcodeScannerFragment : BaseFragment(R.layout.barcode_scanner_fragment) {

    private val binding by viewBinding(BarcodeScannerFragmentBinding::bind)
    private val viewModel by viewModels<BarcodeScannerViewModel>()

    private var cameraSource: CameraSource? = null
    private lateinit var promptChipAnimator: AnimatorSet

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            closeScanner()
        }

        val animatorSource = R.animator.bottom_prompt_chip_enter
        val animatorSet =
            (AnimatorInflater.loadAnimator(requireContext(), animatorSource) as AnimatorSet)

        promptChipAnimator = animatorSet.apply { setTarget(binding.bottomPromptChip) }
        cameraSource = CameraSource(binding.graphicOverlay)

        binding.graphicOverlay.setOnClickListener { startCamera() }
        binding.flashButton.setOnClickListener { onFlashPressed() }
        observeScannerState()
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) != PERMISSION_GRANTED) {
            requestPermission()
        } else {
            startCamera()
        }

        // Emulator testing
        // 036000291452
        // 4036328000015
        /*
        setResult(RESULT_OK, Intent().apply {
            putExtra(KEY_BARCODE, "4036328000015")
        })
        finish()
        */
    }

    override fun onResume() {
        super.onResume()
        startCameraPreview()
    }

    override fun onPause() {
        super.onPause()
        viewModel.setScannerState(ScannerState.NotStarted)
        stopCameraPreview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraSource?.release()
        cameraSource = null
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA), PERMISSION_RESULT)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val result = grantResults.firstOrNull()
        when {
            requestCode != PERMISSION_RESULT -> closeScanner()
            result == PERMISSION_GRANTED -> startCamera()
            result == PERMISSION_DENIED -> showRationale()
            else -> closeScanner()
        }
    }

    private fun showRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), CAMERA)) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.permission_explanation)
                .setPositiveButton(R.string.action_yes) { _, _ -> requestPermission() }
                .setNegativeButton(R.string.action_no) { _, _ -> closeScanner() }
                .show()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.permission_missing)
                .setPositiveButton(R.string.action_ok) { _, _ -> closeScanner() }
                .show()
        }
    }

    private fun startCamera() {
        viewModel.markCameraFrozen()
        cameraSource?.setFrameProcessor(BarcodeProcessor(binding.graphicOverlay, viewModel))
        viewModel.setScannerState(ScannerState.Detecting)
    }

    private fun closeScanner() {
        requireMainActivity().selectMainTab()
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
                    val scanDestination = Destination.Beer(state.beers.first().id)
                    requireMainActivity().setPendingDestination(scanDestination)
                    closeScanner()
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
        private const val PERMISSION_RESULT = 0x500
    }
}
