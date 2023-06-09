package com.example.militaryaccountingapp.presenter.fragment.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.databinding.FragmentItemBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.details.DetailsViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsItemFragment : BaseViewModelFragment<FragmentItemBinding, ViewData, DetailsViewModel>() {

    override val viewModel: DetailsViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentItemBinding
        get() = FragmentItemBinding::inflate

    override fun initializeView() {
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}