package com.example.militaryaccountingapp.presenter.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddItemBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.Barcode
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import com.mcdev.quantitizerlibrary.AnimationStyle
import com.mcdev.quantitizerlibrary.QuantitizerListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemFragment :
    BaseViewModelFragment<FragmentAddItemBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddItemBinding
        get() = FragmentAddItemBinding::inflate


    override fun initializeView() {
        setupQuantity()
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

    private fun setupQuantity() {
        binding.layoutQuantity.apply {
            setQuantitizerListener(object : QuantitizerListener {
                override fun onIncrease() {
                    Toast.makeText(context, "inc", Toast.LENGTH_SHORT).show()
                }

                override fun onDecrease() {
                    Toast.makeText(context, "dec", Toast.LENGTH_SHORT).show()
                }

                override fun onValueChanged(value: Int) {
                    Toast.makeText(context, "value changed to : $value", Toast.LENGTH_SHORT).show()
                }
            })

            textAnimationStyle = AnimationStyle.FALL_IN
        }
    }
}