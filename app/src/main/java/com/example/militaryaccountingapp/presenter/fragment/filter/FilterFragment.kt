package com.example.militaryaccountingapp.presenter.fragment.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentFiltersBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersFilterAdapter
import com.example.militaryaccountingapp.presenter.utils.common.constant.FilterDate
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlin.reflect.full.createInstance


class FilterFragment : BaseViewModelFragment<FragmentFiltersBinding, ViewData, FilterViewModel>()   {

    override val viewModel: FilterViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFiltersBinding
        get() = FragmentFiltersBinding::inflate

    override fun initializeView() {
        setupAdapter()
        setupDateButtons()
        setupDateModal()
    }

    override fun render(data: ViewData) {
        usersAdapter.submitList(data.usersUi)
        userDateSelection = data.filterDate
        binding.selectedDate.text = userDateSelection.displayName
//        binding.buttonSetFilters.isEnabled = data.isFiltersSelected
    }

    private val usersAdapter by lazy {
        UsersFilterAdapter { user, selected ->
            viewModel.changeUserSelection(user, selected)
        }
    }

    private fun setupAdapter(): Unit = binding.rvUsers.run {
        adapter = usersAdapter
        addItemDecoration(
            MaterialDividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

    private var userDateSelection: FilterDate = FilterDate.PickDay()

    private fun setupDateButtons() {
        binding.dateMode.apply {
            addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
                toggleButton.findViewById<MaterialButton>(checkedId).icon =
                    if (isChecked) ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_check_24dp
                    ) else null

                if (isChecked) {
                    if (!binding.buttonSelectDate.isEnabled) {
                        binding.buttonSelectDate.isEnabled = true
                    }

                    when (checkedId) {
                        R.id.button_pick_day -> FilterDate.PickDay::class
                        R.id.button_before -> FilterDate.Before::class
                        R.id.button_after -> FilterDate.After::class
                        R.id.button_range -> FilterDate.Range::class
                        else -> null
                    }?.let { kClass ->
                        kClass.createInstance().also {
                            it.date = userDateSelection.date
//                            it.getDisplayName = userDateSelection.getDisplayName
                            if (it is FilterDate.Range) {
                                it.endDate =  MaterialDatePicker.thisMonthInUtcMilliseconds()
                                it.date =  MaterialDatePicker.todayInUtcMilliseconds()
                            }
                            viewModel.changeDateSelection(it)
                            viewModel.changeFiltersSelected(false)
                        }
                    }
                }
            }
        }
    }

    private fun setupDateModal() {
        binding.buttonSelectDate.setOnClickListener {
            when (userDateSelection) {
                is FilterDate.Range -> showDateRangePicker()
                else -> showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(userDateSelection.date)
            .build()

        picker.addOnPositiveButtonClickListener {
            userDateSelection = userDateSelection::class.createInstance()
            userDateSelection.date = it
            userDateSelection.displayName = picker.headerText
            viewModel.changeDateSelection(userDateSelection)
//            viewModel.changeFiltersSelected(true)
        }

        picker.show(childFragmentManager, picker.toString())
    }

    private fun showDateRangePicker() {
        val range = (userDateSelection as FilterDate.Range)

        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select date")
            .setSelection(Pair(range.endDate, range.date))
            .build()

        picker.addOnPositiveButtonClickListener {
            userDateSelection = FilterDate.Range(it.first, it.second, picker.headerText)
            viewModel.changeDateSelection(userDateSelection)
//            viewModel.changeFiltersSelected(true)
        }

        picker.show(childFragmentManager, picker.toString())
    }
}