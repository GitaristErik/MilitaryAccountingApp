package com.example.militaryaccountingapp.presenter.fragment.camera

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentBarcodeScannerBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BarcodeScannerFragment :
    BaseViewModelFragment<FragmentBarcodeScannerBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentBarcodeScannerBinding
        get() = FragmentBarcodeScannerBinding::inflate


    override fun initializeView() {
        setupActionBar()
        setupScanner()
    }

    private val beepManager by lazy {
        BeepManager(requireActivity())
    }

    private val capture by lazy {
        CaptureManager(requireActivity(), binding.zxingBarcodeScanner)
    }

    private var lastText: String = ""
    private var isTorchOn: Boolean = false

    private val barcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
//            pause()

            // Prevent duplicate scans
            if (result?.text.isNullOrEmpty() || result?.text == lastText) return

            lastText = result?.text ?: ""
            binding.zxingBarcodeScanner.setStatusText(lastText)
            beepManager.playBeepSoundAndVibrate()
            startConfirmDialog()

//            resume()
        }

        override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
        }
    }


    private fun setupScanner() {
//        capture.decode()
        capture

        binding.zxingBarcodeScanner.apply {
            // height edge to edge, without insets
            layoutParams.height = (requireActivity() as AppCompatActivity).window.decorView.height

            setTorchListener(object : TorchListener {
                override fun onTorchOn() {
                    binding.toolbar.menu.findItem(R.id.flash).setIcon(R.drawable.ic_light_off_24dp)
                    isTorchOn = true
                }

                override fun onTorchOff() {
                    binding.toolbar.menu.findItem(R.id.flash).setIcon(R.drawable.ic_light_on_24dp)
                    isTorchOn = false
                }
            })

            decodeContinuous(barcodeCallback)
        }
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean = requireActivity()
        .packageManager
        .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    override fun onResume() {
        super.onResume()
        resume()
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun render(data: ViewData) {
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                back()
            }
            // if the device does not have flashlight in its camera,
            // then remove the switch flashlight button...
            setMenuVisibility(hasFlash())

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.flash -> {
                        if (isTorchOn) {
                            binding.zxingBarcodeScanner.setTorchOff()
                        } else {
                            binding.zxingBarcodeScanner.setTorchOn()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun back() {
        with(requireActivity() as AppCompatActivity) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun pause() {
        capture.onPause()
        binding.zxingBarcodeScanner.pauseAndWait()
    }

    private fun resume() {
        capture.onResume()
        binding.zxingBarcodeScanner.resume()
    }

    private fun startConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm?")
            .setMessage(lastText)
                .setPositiveButton("Yes") { _, _ ->
                viewModel.addCode(lastText)
                back()
            }
            .setNegativeButton("No") { _, _ -> } // resume() }
//            .setOnCancelListener { resume() }
//            .setOnDismissListener { resume() }
            .show()
    }
}
