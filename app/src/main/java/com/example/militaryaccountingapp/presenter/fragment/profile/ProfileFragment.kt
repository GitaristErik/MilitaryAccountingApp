package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.databinding.FragmentProfileBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseViewModelFragment<FragmentProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    override fun initializeView() {
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}