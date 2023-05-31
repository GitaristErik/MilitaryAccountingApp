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
class AddItemFragment : BaseViewModelFragment<FragmentAddItemBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddItemBinding
        get() = FragmentAddItemBinding::inflate


        override fun initializeView() {
        setupQrCodeScanner()

    }

    private fun setupQrCodeScanner() {
//        val scanner = Inte

        // Add the barcode scanner fragment to the view hierarchy.
//        BarcodeView
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}