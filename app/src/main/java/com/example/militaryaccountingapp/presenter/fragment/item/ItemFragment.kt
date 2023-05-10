package com.example.militaryaccountingapp.presenter.fragment.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.databinding.FragmentItemBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.item.ItemViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemFragment : BaseViewModelFragment<FragmentItemBinding, ViewData, ItemViewModel>() {

    override val viewModel: ItemViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentItemBinding
        get() = FragmentItemBinding::inflate

    override fun initializeView() {
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}