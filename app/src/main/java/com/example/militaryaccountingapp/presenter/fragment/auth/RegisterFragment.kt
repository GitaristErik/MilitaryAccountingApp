package com.example.militaryaccountingapp.presenter.fragment.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.registerImagePicker
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentRegisterBinding
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.auth.RegisterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.image.AvatarHelper
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment :
    BaseViewModelFragment<FragmentRegisterBinding, ViewData, RegisterViewModel>() {

    override val viewModel: RegisterViewModel by hiltNavGraphViewModels<RegisterViewModel>(R.id.navigation_auth)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRegisterBinding =
        FragmentRegisterBinding::inflate

    override val provideProgressBar: (() -> View) get() = { binding.loading }


    private lateinit var imagePickerLauncher: ImagePickerLauncher

    override fun initializeView() {
        setupRegister()
        setupFacebook()
        setupGoogle()
        setupToLogin()
        setupPhotoPick()
        setupAutofillHints()
    }

    override fun render(data: ViewData) {
        renderSigned(data.isSigned)
        renderEmail(data.email)
        renderPassword(data.password)
        renderRepassword(data.repassword)
        renderLogin(data.login)
        renderName(data.name)
        renderFullName(data.fullName)
        renderRank(data.rank)
        renderPhones(data.phones)
        renderAvatar(data.imageUri)
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

    private fun setupRegister() = with(binding) {
        register.setOnClickListener {
            viewModel.register(
                email = editEmail.text.toString(),
                password = editPassword.text.toString(),
                repassword = editRepassword.text.toString(),
                login = editLogin.text.toString(),
                name = editName.text.toString(),
                fullName = editFullName.text.toString(),
                rank = editRank.text.toString(),
                phones = phoneList.getPhones()
            )
        }
    }

    private fun setupGoogle() {
        binding.registerWithGoogle.setOnClickListener {
            viewModel.registerWithGoogle(requireActivity())
        }
    }

    private fun setupFacebook() {
        binding.registerWithFacebook.setOnClickListener {
            viewModel.signInWithFacebook(requireActivity())
        }
    }

    private fun setupToLogin() {
        binding.haveAnAccount.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    private fun setupPhotoPick() {
        binding.changeAvatar.setOnClickListener {
            startPhotoPicker()
        }
        binding.deleteAvatar.setOnClickListener {
            viewModel.deleteAvatar()
        }
    }

    private fun setupAutofillHints() {
        binding.editPassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        binding.editRepassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        binding.editEmail.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS)
        binding.editName.setAutofillHints(View.AUTOFILL_HINT_NAME)
        binding.editFullName.setAutofillHints(View.AUTOFILL_HINT_NAME)
    }

    private fun renderSigned(signed: Result<Boolean>) {
        when (signed) {
            is Result.Success -> {
                if (signed.data) {
                    showToast(R.string.register_successful)
                    findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToNavigationHome())
                }
            }

            is Result.Failure -> {
                showToast(getString(R.string.register_failed) + "\n" + signed.throwable.localizedMessage)
            }

            is Result.Canceled -> {
                showToast(getString(R.string.request_canceled) + "\n" + signed.throwable?.localizedMessage)
            }

            else -> {}
        }
    }

    private fun renderEmail(email: Result<String>) {
        binding.editEmail.renderValidate(email)
    }

    private fun renderPassword(password: Result<String>) {
        binding.editPassword.renderValidate(password, binding.passwordLayout)
    }

    private fun renderRepassword(repassword: Result<String>) {
        binding.editRepassword.renderValidate(repassword, binding.repasswordLayout)
    }

    private fun renderLogin(login: Result<String>) {
        binding.editLogin.renderValidate(login, binding.loginLayout)
    }

    private fun renderRank(rank: Result<String>) {
        binding.editRank.renderValidate(rank)
    }

    private fun renderFullName(fullname: Result<String>) {
        binding.editFullName.renderValidate(fullname)
    }

    private fun renderName(name: Result<String>) {
        binding.editName.renderValidate(name)
    }

    private fun renderPhones(phones: List<Result<String>>) {
        binding.phoneList.renderValidate(phones)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }

    private fun startPhotoPicker() {
        imagePickerLauncher.launch(AvatarHelper.createPickerConfig())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imagePickerLauncher = registerImagePicker {
            findNavController().navigate(
                R.id.action_registerFragment_to_cropImageFragment,
                Bundle().apply {
                    putParcelable(
                        AvatarHelper.URI_USER,
                        it.firstOrNull()?.uri ?: return@registerImagePicker
                    )
                }
            )
        }
    }
}