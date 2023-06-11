package com.example.militaryaccountingapp.presenter.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.ModalBottomSheetCodeBinding
import com.example.militaryaccountingapp.presenter.model.Barcode
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import com.example.militaryaccountingapp.presenter.utils.ui.ext.initAsQrFull
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ModalBottomSheetCodeDetails : BottomSheetDialogFragment() {

    private val binding: ModalBottomSheetCodeBinding by lazy {
        ModalBottomSheetCodeBinding.inflate(layoutInflater)
    }

    private val viewModel: AddOrEditViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            initializeView()
        }
    }

    private fun initializeView() = binding.run {
        (requireArguments().getSerializable("code") as? Barcode)?.let { barcode ->
            date.text = barcode.timestamp.asFormattedDateString()
            qrData.text = barcode.code
            qrImage.initAsQrFull(barcode.code)
            qrDelete.setOnClickListener {
                startDeleteDialog {
                    viewModel.deleteCode(barcode)
                    dismiss()
                }
            }
        }
    }

    private fun startDeleteDialog(onDelete: (() -> Unit)? = null) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.add_code_alert_title))
            .setMessage(getString(R.string.add_code_alert_message))
            .setPositiveButton(getString(R.string.add_code_alert_positive)) { _, _ ->
                onDelete?.invoke()
            }
            .setNegativeButton(getString(R.string.add_code_alert_negative)) { _, _ -> }
            .show()
    }
}