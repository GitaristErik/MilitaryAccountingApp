package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentDetailsUserBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsUserFragment :
    BaseViewModelFragment<FragmentDetailsUserBinding, ViewData, DetailsUserViewModel>() {

    override val viewModel: DetailsUserViewModel by activityViewModels<DetailsUserViewModel>()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailsUserBinding
        get() = FragmentDetailsUserBinding::inflate

    override fun initializeView() {
        setupActionBar()
    }

    override fun render(data: ViewData) {
        log.d("render")
        ProfileHelper.setupAvatarWithIntent(
            requireActivity(),
            binding.avatar,
            data.userProfileUri
        )
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