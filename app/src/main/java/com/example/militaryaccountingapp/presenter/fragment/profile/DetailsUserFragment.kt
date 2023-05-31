package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentDatailsUserBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsUserFragment :
    BaseViewModelFragment<FragmentDatailsUserBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDatailsUserBinding
        get() = FragmentDatailsUserBinding::inflate

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

    override fun render(data: ViewData) {
        log.d("render")
    }
}