package com.example.militaryaccountingapp.presenter.fragment.profile

import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentCropUserAvatarBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import com.isseiaoki.simplecropview.callback.LoadCallback
import com.isseiaoki.simplecropview.callback.SaveCallback
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CropUserAvatarFragment :
    BaseViewModelFragment<FragmentCropUserAvatarBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by navGraphViewModels(R.id.mobile_navigation)
//    override val viewModel: ProfileViewModel by activityViewModels<ProfileViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCropUserAvatarBinding
        get() = FragmentCropUserAvatarBinding::inflate


    override fun initializeView() {
        setupActionBar()
        setupCropper()
    }

    private val uri by lazy {
        requireArguments().getParcelable<Uri>("uri")
            ?: throw IllegalArgumentException("Uri is null")
    }

    private fun setupCropper() {
        binding.cropView.apply {
            // height edge to edge, without insets
            layoutParams.height = (requireActivity() as AppCompatActivity).window.decorView.height

            load(uri)
                .useThumbnail(true)
                .execute(object : LoadCallback {
                    override fun onError(e: Throwable?): Unit = onErrorHandler(e)
                    override fun onSuccess() {
                        log.d("onSuccess, sourceUri: $sourceUri")
                    }
                })
        }
    }

     fun onErrorHandler(e: Throwable?) {
        log.e("onError: $e")
        Toast.makeText(requireContext(), e?.message, Toast.LENGTH_SHORT).show()
    }

    private fun cropAndSave(saveUri: Uri, onSaveCallback: ((Uri) -> Unit)?) {
        binding.cropView.crop(uri).execute(
            object : CropCallback {
                override fun onError(e: Throwable?): Unit = onErrorHandler(e)

                override fun onSuccess(cropped: Bitmap?) {
                    binding.cropView.save(cropped).execute(saveUri, object : SaveCallback {
                        override fun onError(e: Throwable?): Unit = onErrorHandler(e)

                        override fun onSuccess(savedUri: Uri?) {
                            log.d("onSuccess, uri: $savedUri")
                            viewModel.setAvatar(savedUri ?: return)
                            onSaveCallback?.invoke(savedUri)
                        }
                    })
                }
            }
        )
    }

    override fun render(data: ViewData) {
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                back()
            }

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.done -> {
                        cropAndSave(uri) { back() }
                        true
                    }
                    R.id.rotate_left -> {
                        binding.cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D)
                        true
                    }
                    R.id.rotate_right -> {
                        binding.cropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D)
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
}
