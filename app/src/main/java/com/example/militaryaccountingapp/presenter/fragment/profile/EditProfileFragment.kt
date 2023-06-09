package com.example.militaryaccountingapp.presenter.fragment.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateMargins
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ImagePickerSavePath
import com.esafirm.imagepicker.features.registerImagePicker
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentEditProfileBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileFragment :
    BaseViewModelFragment<FragmentEditProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileBinding
        get() = FragmentEditProfileBinding::inflate

    private lateinit var imagePickerLauncher: ImagePickerLauncher

    override fun initializeView() {
        setupActionBar()
        setupPhone()
        setupPhotoPick()
    }

    override fun render(data: ViewData) {
        log.d("render")
        log.d("render data test: ${data.test}")
        setupAvatar(data.userProfileUri)
    }

    private fun setupAvatar(uri: Uri?) {
        log.d("setupAvatar uri: $uri")
        if (uri == null) return
        Glide.with(this)
            .load(uri)
            .circleCrop()
            .into(binding.avatar)

        binding.avatar.setOnClickListener {
            showAvatar(uri)
        }
    }

    private fun showAvatar(uri: Uri) {
        requireActivity().startActivity(
            getUriViewIntent(requireContext(), uri) ?: return
        )
    }

    private fun setupPhotoPick() {
        binding.changeAvatar.setOnClickListener {
//            startPhotoPicker(getCameraOptions())
            startPhotoPicker()
        }
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun startPhotoPicker() {
        imagePickerLauncher.launch(createPickerConfig())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imagePickerLauncher = registerImagePicker {
            navigateToCropFragment(it.firstOrNull()?.uri ?: return@registerImagePicker)
        }
    }

    private fun createPickerConfig() = ImagePickerConfig {
        // default is multi image mode
        mode = ImagePickerMode.SINGLE
        // Set image picker language
        language = "en"
        theme = R.style.Theme_MilitaryAccountingApp

        // set whether pick action or camera action should return immediate result or not.
        // Only works in single mode for image picker
//        returnMode = if (returnAfterCapture) ReturnMode.ALL else ReturnMode.NONE

        // set folder mode (false by default)
        isFolderMode = false
        // include video (false by default)
        isIncludeVideo = false
        isOnlyVideo = false
//      set toolbar arrow up color
//      arrowColor = requireActivity().getColor(R.id.color)
        // folder selection title
        folderTitle = "Folder"
        imageTitle = "Tap to select"
        doneButtonText = "Done"
        // max images can be selected (99 by default)
        limit = 1
        // show camera or not (true by default)
        isShowCamera = true
        // captured image directory name ("Camera" folder by default)
        savePath = ImagePickerSavePath(SAVE_PATH)
        savePath = ImagePickerSavePath(
            Environment.getExternalStorageDirectory().path,
            isRelative = false
        ) // can be a full path
    }

    private fun navigateToCropFragment(uri: Uri) {
        findNavController().navigate(
            R.id.action_editProfileFragment_to_cropUserAvatarFragment,
            Bundle().apply { putParcelable("uri", uri) }
        )
    }

    private fun setupPhone() {
        binding.addPhoneLayout.setEndIconOnClickListener {
            if (binding.addPhone.text.isNullOrEmpty()) return@setEndIconOnClickListener
            makePhoneEditText(binding.addPhone.text.toString()) {
                binding.phoneContainer.removeView(it)
            }.also {
                binding.phoneContainer.addView(it)
                binding.addPhone.setText("")
            }
        }
    }

    private fun makePhoneEditText(
        phone: String,
        onRemoveListener: OnClickListener
    ): TextInputEditText =
        TextInputEditText(requireContext()).apply {
            val id = Companion.generateId()
            setId(id)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = resources.getDimension(R.dimen.margin_small_extra).toInt()
                updateMargins(top = margin, bottom = margin)
            }
            isElegantTextHeight = true
            hint = getString(R.string.profile_phone_hint)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setText(phone)

            // set drawable and listener
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_remove_24dp, 0
            )
            compoundDrawablePadding = resources.getDimension(R.dimen.padding_small).toInt()
            setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[2].bounds.width())) {
                        onRemoveListener.onClick(this)
                        return@setOnTouchListener true
                    } else {
                        view?.performClick()
                    }
                }
                return@setOnTouchListener false
            }
        }

    companion object {
        private const val SAVE_PATH = "Camera"

        /**
         * Get Intent to View Uri backed File
         *
         * @param context
         * @param uri
         * @return Intent
         */
        @JvmStatic
        fun getUriViewIntent(context: Context, uri: Uri): Intent? {
            return if (DocumentFile.fromSingleUri(context, uri)?.canRead() == true) {
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "image/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else null
        }


        private fun generateId(): Int {
            // Get the current time in milliseconds.
            val time = System.currentTimeMillis()

            // Get the current thread ID.
            val threadId = Thread.currentThread().id

            // Return a unique ID that is based on the current time and thread ID.
            return (time * 10000 + threadId).toInt()
        }
    }
}
