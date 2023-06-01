package com.example.militaryaccountingapp.presenter.fragment.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.navGraphViewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddItemBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
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
        setupQrCodeScanner()
    }

    private fun setupQrCodeScanner() {
//        val scanner = Inte

        // Add the barcode scanner fragment to the view hierarchy.
//        BarcodeView
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

    override fun render(data: ViewData) {
        log.d("render")
    }
}