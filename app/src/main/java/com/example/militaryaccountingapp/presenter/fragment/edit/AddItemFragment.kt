package com.example.militaryaccountingapp.presenter.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
        setupTitle()
        setupDescription()
    }

    private var isDataInit = false

    override fun render(data: ViewData) {
        log.d("render")
        codesAdapter.submitList(data.codes.toList())

        if (isDataInit) return
        else isDataInit = true

        binding.editTitle.setText(data.title)
        binding.editDescription.setText(data.description)
        binding.layoutQuantity.value = data.count
    }

    override fun onStop() {
        super.onStop()
        isDataInit = false
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
                override fun onValueChanged(value: Int) {
                    viewModel.setCount(value)
                }

                override fun onIncrease() {}
                override fun onDecrease() {}
            })

            textAnimationStyle = AnimationStyle.FALL_IN
        }
    }

    private fun setupTitle() {
        binding.editTitle.addTextChangedListener {
            viewModel.setTitle(it.toString())
        }
    }

    private fun setupDescription() {
        binding.editDescription.addTextChangedListener {
            viewModel.setDescription(it.toString())
        }
    }
}