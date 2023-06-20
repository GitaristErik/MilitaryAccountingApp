package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentDetailsUserBinding
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.image.AvatarHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsUserFragment :
    BaseViewModelFragment<FragmentDetailsUserBinding, ViewData, DetailsUserViewModel>() {

    override val viewModel: DetailsUserViewModel by activityViewModels<DetailsUserViewModel>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsUserBinding
        get() = FragmentDetailsUserBinding::inflate

    override val provideProgressBar: (() -> View) get() = { binding.loading }

    override fun initializeView() {
        setupActionBar()
        sendArgs()
        setupMemberActions()
    }

    private fun setupMemberActions() {
        binding.addMember.setOnClickListener {
            viewModel.addMember()
        }
        binding.deleteMember.setOnClickListener {
            viewModel.deleteMember()
        }
    }

    private fun sendArgs() {
        val userid = requireArguments().getString("userId")!!
        val name = requireArguments().getString("name")
        val fullName = requireArguments().getString("fullName")
        val rank = requireArguments().getString("rank")
//        val image

        viewModel.sendTempUser(userid, name, fullName, rank)
    }

    override fun render(data: ViewData) {
        log.d("render")
        AvatarHelper.setupAvatarWithIntent(
            requireActivity(),
            binding.avatar,
            data.userProfileUri
        )

        renderUser(data.user)
        renderInNetwork(data.inNetwork)
    }

    private fun renderInNetwork(inNetwork: Results<Boolean>) {
        when (inNetwork) {
            is Results.Success -> {
                if (inNetwork.data) {
                    binding.inNetwork.text = getString(R.string.profile_innetwork_text)
                    binding.inNetwork.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.md_primary
                        )
                    )
                    binding.deleteMember.visibility = View.VISIBLE
                    binding.addMember.visibility = View.GONE
                } else {
                    binding.inNetwork.text = getString(R.string.profile_nonnetwork_text)
                    binding.inNetwork.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.md_secondary
                        )
                    )
                    binding.deleteMember.visibility = View.GONE
                    binding.addMember.visibility = View.VISIBLE
                }
            }

            else -> {
            }
        }
    }


    private fun renderUser(user: Results<User>) {
        when (user) {
            is Results.Success -> {
                binding.userName.text = user.data.name
                binding.fullName.text = user.data.fullName
                binding.userRank.text = user.data.rank

                binding.email.text = user.data.email
                binding.phone.text = user.data.phones.joinToString("\n")
            }

            is Results.Loading -> {
                binding.userName.text = user.oldData?.name ?: ""
                binding.fullName.text = user.oldData?.fullName ?: ""
                binding.userRank.text = user.oldData?.rank ?: ""
            }

            is Results.Failure -> {
                Snackbar.make(
                    binding.root,
                    user.throwable.message ?: "Error while getting the user details!",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

            else -> {

            }
        }
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit -> {
                            // Handle edit text press
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }
}