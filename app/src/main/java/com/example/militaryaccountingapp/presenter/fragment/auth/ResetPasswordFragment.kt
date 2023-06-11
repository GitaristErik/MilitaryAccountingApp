package com.example.militaryaccountingapp.presenter.fragment.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentResetPasswordBinding
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.auth.ResetPasswordViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment :
    BaseViewModelFragment<FragmentResetPasswordBinding, ViewData, ResetPasswordViewModel>() {

    override val viewModel: ResetPasswordViewModel by hiltNavGraphViewModels<ResetPasswordViewModel>(
        R.id.navigation_auth
    )

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
            is Results.Success -> {
                if (data.isReset.data) {
                    showToast(R.string.login_successful)
                    findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment())
                }
            }

            is Results.Failure -> {
                data.isReset.throwable.localizedMessage?.let(::showToast)
            }

            is Results.Canceled -> {
                showToast(getString(R.string.request_canceled) + "<br/>" + data.isReset.throwable?.localizedMessage)
            }

            else -> {}
        }
    }
}