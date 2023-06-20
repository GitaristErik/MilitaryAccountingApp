package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentProfileBinding
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.UserNetworkUi
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersNetworkAdapter
import com.example.militaryaccountingapp.presenter.utils.image.AvatarHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment() :
    BaseViewModelFragment<FragmentProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    override fun initializeView() {
        setupChangeProfile()
        setupAddMember()
        setupLogout()
        setupUsersNetwork()
        viewModel.fetchUserData()
    }

    private val usersAdapter by lazy {
        UsersNetworkAdapter {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavigationProfileToDetailsUserFragment(
                    userId = it.id,
                    fullName = it.fullName,
                    rank = it.rank,
//                    name = it.name,
                )
            )
        }
    }

    private fun setupUsersNetwork() {
        binding.networkUsers.adapter = usersAdapter
    }

    private fun setupChangeProfile() {
        binding.buttonChangeProfile.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToEditProfileFragment())
        }
    }

    private fun setupAddMember() {
        binding.addMember.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToUsersListFragment())
        }
    }

    private fun setupLogout() {
        binding.logout.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
        if (data.isLoggingOut is Results.Success) {
            findNavController().apply {
                navigate(R.id.action_navigation_profile_to_loginFragment)
            }
        }
        AvatarHelper.setupAvatarWithIntent(
            requireActivity(),
            binding.avatar,
            data.userProfileUri
        )

        renderUser(data.user)
        renderUsers(data.usersNetwork)
    }

    private fun renderUsers(usersNetwork: Results<List<UserNetworkUi>>) {
        when (usersNetwork) {
            is Results.Success -> {
                log.d("renderUsers in network: ${usersNetwork.data}")
                usersAdapter.submitList(usersNetwork.data)
            }

            else -> {}
        }
    }

    private fun renderUser(userRes: Results<User>) {
        when (userRes) {
            is Results.Success -> {
                renderUser(userRes.data)
            }

            is Results.Failure -> {
                log.e("renderUser: ${userRes.throwable.message}")
            }

            else -> {}
        }
    }

    private fun renderUser(user: User) {
        binding.userName.text = user.fullName
        binding.userRank.text = user.rank
    }
}