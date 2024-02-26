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
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import quickbeer.android.R
import quickbeer.android.databinding.BarcodeScannerFragmentBinding
import quickbeer.android.feature.barcode.camera.CameraSource
import quickbeer.android.feature.barcode.detection.BarcodeProcessor
import quickbeer.android.navigation.Destination
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.setNegativeAction
import quickbeer.android.util.ktx.setPositiveAction
import quickbeer.android.util.ktx.viewBinding
import timber.log.Timber

@AndroidEntryPoint
class BarcodeScannerFragment : BaseFragment(R.layout.barcode_scanner_fragment) {

    private val binding by viewBinding(BarcodeScannerFragmentBinding::bind)
    private val viewModel by viewModels<BarcodeScannerViewModel>()

    private var cameraSource: CameraSource? = null
    private lateinit var promptChipAnimator: AnimatorSet
    private var scanJob: Job? = null

    private val permissionHandler = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupCamera()
        } else {
            showRationale()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            closeScanner()
        }

        binding.graphicOverlay.setOnClickListener {
            if (!viewModel.isCameraLive) startCamera()
        }

        binding.flashButton.setOnClickListener { onFlashPressed() }
    }

    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(requireContext(), CAMERA) != PERMISSION_GRANTED) {
            requestPermission()
        } else {
            setupCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setScannerState(ScannerState.NotStarted)
        stopCameraPreview()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        scanJob?.cancel()
        scanJob = null

        cameraSource?.release()
        cameraSource = null
    }

    private fun requestPermission() {
        permissionHandler.launch(CAMERA)
    }

    private fun showRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), CAMERA)) {
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage(R.string.permission_explanation)
                .setPositiveAction(R.string.action_yes, ::requestPermission)
                .setNegativeAction(R.string.action_no) { closeScanner() }
                .show()
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.permission_missing)
                .setPositiveAction(R.string.action_ok, ::closeScanner)
                .show()
        }
    }

    private fun setupCamera() {
        val animatorSource = R.animator.bottom_prompt_chip_enter
        val animatorSet =
            (AnimatorInflater.loadAnimator(requireContext(), animatorSource) as AnimatorSet)

        promptChipAnimator = animatorSet.apply { setTarget(binding.bottomPromptChip) }
        cameraSource = CameraSource(binding.graphicOverlay)

        showPrompt(getString(R.string.prompt_loading_camera))
        startCamera()
    }

    private fun startCamera() {
        viewModel.markCameraFrozen()
        cameraSource?.setFrameProcessor(BarcodeProcessor(binding.graphicOverlay, viewModel))
        viewModel.setScannerState(ScannerState.Detecting)

        scanJob?.cancel()
        scanJob = lifecycleScope.launch {
            viewModel.scan().collect(::handleScannerState)
        }

        requireView().postDelayed(Runnable(::startCameraPreview), 150)
    }

    private fun closeScanner() {
        requireMainActivity().selectMainTab()
    }

    private fun onFlashPressed() {
        val selected = !binding.flashButton.isSelected
        val mode = if (selected) {
            Camera.Parameters.FLASH_MODE_TORCH
        } else {
            Camera.Parameters.FLASH_MODE_OFF
        }

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

    private fun handleScannerState(state: ScannerState) {
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
                showPrompt(getString(R.string.prompt_no_results).format(state.barcode))
                stopCameraPreview()
            }
            is ScannerState.Error -> {
                showPrompt(getString(R.string.prompt_error).format(state.barcode))
                stopCameraPreview()
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
