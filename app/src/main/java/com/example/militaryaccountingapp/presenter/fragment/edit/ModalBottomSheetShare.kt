package com.example.militaryaccountingapp.presenter.fragment.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.databinding.ModalBottomShareBinding
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModalBottomSheetShare : BottomSheetDialogFragment() {

    private val binding: ModalBottomShareBinding by lazy {
        ModalBottomShareBinding.inflate(layoutInflater)
    }

    companion object {
        const val REQUEST_KEY = "ModalBottomSheetShare_REQUEST_KEY"
        const val PERMISSION = "permission"
        const val USER_ID = "userId"
    }


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


    private val permission: UserPermission? by lazy {
        requireArguments().getSerializable("permission") as? UserPermission
    }
    private val userId: String by lazy {
        requireArguments().getString("userId")!!
    }
    private val itemId: String? by lazy {
        requireArguments().getString("itemId")
    }
    private val categoryId: String? by lazy {
        requireArguments().getString("categoryId")
    }

    private fun initializeView() = binding.run {
//        setCheckboxesEnabled(false)
        initCheckboxes()

        setRules.setOnClickListener {
            val permission = if (checkboxRead.isChecked) {
                UserPermission(
                    userId = userId,
                    itemId = itemId,
                    categoryId = categoryId,
                    canWrite = checkboxEdit.isChecked,
                    canShare = checkboxReadShare.isChecked,
                    canShareForWrite = checkboxEditShare.isChecked,
                )
            } else null

            setFragmentResult(
                REQUEST_KEY, bundleOf(PERMISSION to permission, USER_ID to userId)
            )

            // Step 4. Go back to Fragment A
            findNavController().navigateUp()
//            dismiss()
        }
    }

    private fun initCheckboxes() {
        binding.checkboxRead.isChecked = permission?.let { true } ?: false
        binding.checkboxEdit.isChecked = permission?.canWrite ?: false
        binding.checkboxReadShare.isChecked = permission?.canShare ?: false
        binding.checkboxEditShare.isChecked = permission?.canShareForWrite ?: false
    }
}