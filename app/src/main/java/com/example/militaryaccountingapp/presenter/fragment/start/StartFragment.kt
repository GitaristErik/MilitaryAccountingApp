package com.example.militaryaccountingapp.presenter.fragment.start

import com.example.militaryaccountingapp.domain.helper.Result
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.databinding.FragmentStartBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.start.StartViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment @Inject constructor() :
    BaseViewModelFragment<FragmentStartBinding, ViewData, StartViewModel>() {

    override val viewModel: StartViewModel by activityViewModels<StartViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStartBinding =
        FragmentStartBinding::inflate

    override val provideProgressBar: (() -> View) get() = { binding.loading }

    override fun initializeView() {
    }

    override fun render(data: ViewData) {
        if(data.currentUser !is Result.Success) return

        findNavController().navigate(
            if (data.currentUser.data == null) {
                StartFragmentDirections.actionStartFragmentToLoginFragment()
            } else {
                StartFragmentDirections.actionStartFragmentToNavigationHome()
            }
        )
    }
}