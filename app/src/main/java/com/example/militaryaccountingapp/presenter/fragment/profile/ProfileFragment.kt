package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentProfileBinding
import com.example.militaryaccountingapp.domain.helper.Result
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment :
    BaseViewModelFragment<FragmentProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by activityViewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    override fun initializeView() {
        setupChangeProfile()
        setupAddMember()
        setupLogout()
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

    private fun setupLogout() {
        binding.logout.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
        if (data.isLoggingOut is Result.Success) {
            findNavController().navigate(R.id.action_navigation_profile_to_loginFragment)
        }
        ProfileHelper.setupAvatarWithIntent(
            requireActivity(),
            binding.avatar,
            data.userProfileUri
        )
    }
}