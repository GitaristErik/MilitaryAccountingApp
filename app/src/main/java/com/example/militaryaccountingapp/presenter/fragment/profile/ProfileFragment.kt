package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentProfileBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseViewModelFragment<FragmentProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by navGraphViewModels<ProfileViewModel>(R.id.mobile_navigation)
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    override fun initializeView() {
        setupChangeProfile()
        setupAddMember()
    }

    private fun setupChangeProfile() {
        binding.buttonChangeProfile.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment())
        }
    }

    private fun setupAddMember() {
        binding.addMember.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToDetailsUserFragment())
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
        ProfileHelper.setupAvatarWithIntent(
            requireActivity(),
            binding.avatar,
            data.userProfileUri
        )
    }
}