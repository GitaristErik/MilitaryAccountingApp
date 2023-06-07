package com.example.militaryaccountingapp.presenter.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddCategoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.Barcode
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryFragment : BaseViewModelFragment<FragmentAddCategoryBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddCategoryBinding
        get() = FragmentAddCategoryBinding::inflate

    override fun initializeView() {
        setupCodeScanner()
        setupCodes()
    }

    override fun render(data: ViewData) {
        log.d("render")
        codesAdapter.submitList(data.codes.toList())
    }

    private val codesAdapter by lazy {
        BarCodeAdapter { barcode ->
            showCodeDetails(barcode)
        }
    }

    private fun showCodeDetails(barcode: Barcode) {
        log.d("showCodeDetails : $barcode")
        findNavController().navigate(
            R.id.action_addFragment_to_modalBottomSheetCodeDetails,
            Bundle().apply {
                putSerializable("code", barcode)
            }
        )
    }

    private fun setupCodes() {
        binding.rvCodes.adapter = codesAdapter
    }

    private fun setupCodeScanner() {
        binding.buttonScan.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_barcodeScannerFragment)
        }
    }
}