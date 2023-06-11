package com.example.militaryaccountingapp.presenter.fragment.auth

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentLoginBinding
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.auth.LoginViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseViewModelFragment<FragmentLoginBinding, ViewData, LoginViewModel>() {

    override val viewModel: LoginViewModel by hiltNavGraphViewModels<LoginViewModel>(R.id.navigation_auth)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding =
        FragmentLoginBinding::inflate

    override val provideProgressBar: (() -> View) get() = { binding.loading }

    override fun initializeView() {
        binding.login.setOnClickListener {
            viewModel.login(
                binding.email.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.loginWithFacebook.setOnClickListener {
            viewModel.signInWithFacebook(requireActivity())
        }

        binding.loginWithGoogle.setOnClickListener {
            viewModel.loginWithGoogle(requireActivity())
        }

        binding.loginRegisterNow.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
        }

        binding.password.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
        binding.email.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS)
    }

    override fun render(data: ViewData) {
        renderSigned(data.isSigned)
        renderEmail(data.email)
        renderPassword(data.password)
    }

    private fun renderSigned(signed: Result<Boolean>) {
        when (signed) {
            is Result.Success -> {
                if (signed.data) {
                    showToast(R.string.login_successful)
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNavigationHome())
                }
                binding.error.visibility = View.GONE
            }

            is Result.Failure -> {
                showToast(getString(R.string.login_failed) + "\n" + signed.throwable.localizedMessage)
                binding.error.visibility = View.VISIBLE
            }

            is Result.Canceled -> {
                showToast(getString(R.string.request_canceled) + "\n" + signed.throwable?.localizedMessage)
                binding.error.visibility = View.GONE
            }

            else -> {
                binding.error.visibility = View.GONE
            }
        }
    }

    private fun renderEmail(email: Result<String>) {
        binding.email.renderValidate(email)
    }

    private fun renderPassword(password: Result<String>) {
        binding.password.renderValidate(password)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.onActivityResult(requestCode, resultCode, data)
    }
}