package com.example.militaryaccountingapp.presenter.fragment.profile

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateMargins
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentEditProfileBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.ProfileViewModel.ViewData
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment :
    BaseViewModelFragment<FragmentEditProfileBinding, ViewData, ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEditProfileBinding
        get() = FragmentEditProfileBinding::inflate

    override fun initializeView() {
        setupActionBar()
        setupPhone()
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
//            setSupportActionBar(binding.toolbar)
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun setupPhone() {
        binding.addPhoneLayout.setEndIconOnClickListener {
            if(binding.addPhone.text.isNullOrEmpty()) return@setEndIconOnClickListener
            makePhoneEditText(binding.addPhone.text.toString()) {
                binding.phoneContainer.removeView(it)
            }.also {
                binding.phoneContainer.addView(it)
                binding.addPhone.setText("")
            }
        }
    }

    private fun makePhoneEditText(
        phone: String,
        onRemoveListener: OnClickListener
    ): TextInputEditText =
        TextInputEditText(requireContext()).apply {
            val id = generateId()
            setId(id)
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = resources.getDimension(R.dimen.margin_small_extra).toInt()
                updateMargins(top = margin, bottom = margin)
            }
            isElegantTextHeight = true
            hint = getString(R.string.profile_phone_hint)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setText(phone)

            // set drawable and listener
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0, 0, R.drawable.ic_remove_24dp, 0
            )
            compoundDrawablePadding = resources.getDimension(R.dimen.padding_small).toInt()
            setOnTouchListener { _, event ->
                if (event.action == android.view.MotionEvent.ACTION_UP) {
                    if (event.rawX >= (right - compoundDrawables[2].bounds.width())) {
                        onRemoveListener.onClick(this)
                        return@setOnTouchListener true
                    } else {
                        view?.performClick()
                    }
                }
                return@setOnTouchListener false
            }
        }

    private fun generateId(): Int {
        // Get the current time in milliseconds.
        val time = System.currentTimeMillis()

        // Get the current thread ID.
        val threadId = Thread.currentThread().id

        // Return a unique ID that is based on the current time and thread ID.
        return (time * 10000 + threadId).toInt()
    }


    override fun render(data: ViewData) {
        log.d("render")
    }
}