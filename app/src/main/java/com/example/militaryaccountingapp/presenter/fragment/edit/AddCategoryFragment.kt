package com.example.militaryaccountingapp.presenter.fragment.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddItemBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryFragment : BaseViewModelFragment<FragmentAddItemBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddItemBinding
        get() = FragmentAddItemBinding::inflate


    override fun initializeView()  {

    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}