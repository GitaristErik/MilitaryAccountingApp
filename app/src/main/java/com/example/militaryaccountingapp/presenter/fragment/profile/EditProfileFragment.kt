package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.databinding.FragmentEditProfileBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment :
    BaseViewModelFragment<FragmentEditProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileBinding
        get() = FragmentEditProfileBinding::inflate

    override fun initializeView() {
        setupActionBar()
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
//            setSupportActionBar(binding.toolbar)
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}