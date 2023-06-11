package com.example.militaryaccountingapp.presenter.fragment.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentResetPasswordBinding
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.auth.ResetPasswordViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate

class ResetPasswordFragment :
    BaseViewModelFragment<FragmentResetPasswordBinding, ViewData, ResetPasswordViewModel>() {

    override val viewModel: ResetPasswordViewModel by activityViewModels<ResetPasswordViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentResetPasswordBinding =
        FragmentResetPasswordBinding::inflate

    override val provideProgressBar: (() -> View) get() = { binding.loading }

    override fun initializeView() {
        binding.reset.setOnClickListener {
            viewModel.reset(
                binding.email.text.toString()
            )
        }

        binding.loginNow.setOnClickListener {
            findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment())
        }

        binding.email.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS)
    }

    override fun render(data: ViewData) {
        binding.email.renderValidate(data.email)

        when (data.isReset) {
            is Result.Success -> {
                if (data.isReset.data) {
                    showToast(R.string.login_successful)
                    findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment())
                }
            }

            is Result.Failure -> {
                data.isReset.throwable.localizedMessage?.let(::showToast)
            }

            is Result.Canceled -> {
                showToast(getString(R.string.request_canceled) + "<br/>" + data.isReset.throwable?.localizedMessage)
            }

            else -> {}
        }
    }
}