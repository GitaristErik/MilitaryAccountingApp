package com.example.militaryaccountingapp.presenter.fragment.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.militaryaccountingapp.databinding.FragmentEditBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EditFragment : BaseViewModelFragment<FragmentEditBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditBinding
        get() = FragmentEditBinding::inflate


    override fun initializeView() {
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}