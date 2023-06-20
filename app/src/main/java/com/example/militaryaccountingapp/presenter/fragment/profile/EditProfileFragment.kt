package com.example.militaryaccountingapp.presenter.fragment.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.registerImagePicker
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentEditProfileBinding
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.image.AvatarHelper
import com.example.militaryaccountingapp.presenter.utils.image.AvatarHelper.createPickerConfig
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditProfileFragment :
    BaseViewModelFragment<FragmentEditProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by activityViewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileBinding
        get() = FragmentEditProfileBinding::inflate

    private lateinit var imagePickerLauncher: ImagePickerLauncher

    override val provideProgressBar: (() -> View) get() = { binding.loading }


    override fun initializeView() {
        log.d("viewModel link $viewModel")
        setupActionBar()
        setupPhotoPick()
        setupAutofillHints()
    }

    override fun render(data: ViewData) {
        log.d("render edit user $data")
        renderEdit(data.isEdited)
        renderEmail(data.email)
        renderName(data.name)
        renderFullName(data.fullName)
        renderRank(data.rank)
        renderPhones(data.phones)
        renderAvatar(data.userProfileUri)
    }

    private fun renderEdit(results: Results<Unit>) {
        log.d("render")
        binding.loading.visibility = View.GONE
        when (results) {
            is Results.Success -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            is Results.Failure -> {
                showToast(results.throwable.localizedMessage ?: "Error while editing profile")
            }

            is Results.Loading -> {
                binding.loading.visibility = View.VISIBLE
            }

            else -> {}
        }
    }

    private fun renderAvatar(imageUri: Uri?) {
        log.d("render")
        binding.deleteAvatar.visibility = if (imageUri == null) View.GONE else View.VISIBLE
        AvatarHelper.setupAvatarWithIntent(
            requireActivity(),
            binding.avatar,
            imageUri
        )
    }

    private fun setupAutofillHints() {
        binding.editPassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        binding.editRepassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        binding.editEmail.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS)
        binding.editName.setAutofillHints(View.AUTOFILL_HINT_NAME)
        binding.editFullName.setAutofillHints(View.AUTOFILL_HINT_NAME)
    }

    private fun setupPhotoPick() {
        binding.changeAvatar.setOnClickListener {
            startPhotoPicker()
        }
        binding.deleteAvatar.setOnClickListener {
            viewModel.deleteAvatar()
        }
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> with(binding) {
                            viewModel.save(
                                email = editEmail.text.toString(),
                                name = editName.text.toString(),
                                fullName = editFullName.text.toString(),
                                rank = editRank.text.toString(),
                                phones = phoneList.getPhones()
                            )
                            true
                        }

                        else -> false
                    }
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

    private fun navigateToCropFragment(uri: Uri) {
        findNavController().navigate(
            R.id.action_editProfileFragment_to_cropImageFragment,
            Bundle().apply { putParcelable("uri_avatar", uri) }
        )
    }

    private fun renderEmail(email: Results<String>) {
        binding.editEmail.renderValidate(email)
    }

    private fun renderRank(rank: Results<String>) {
        binding.editRank.renderValidate(rank)
    }

    private fun renderFullName(fullname: Results<String>) {
        binding.editFullName.renderValidate(fullname)
    }

    private fun renderName(name: Results<String>) {
        binding.editName.renderValidate(name)
    }

    private fun renderPhones(phones: List<Results<String>>) {
        binding.phoneList.renderValidate(phones)
    }
}
