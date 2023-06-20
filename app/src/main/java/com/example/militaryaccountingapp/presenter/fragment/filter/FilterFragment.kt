package com.example.militaryaccountingapp.presenter.fragment.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.militaryaccountingapp.databinding.FragmentFiltersBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersFilterAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
// TODO change to bottom sheet
class FilterFragment @Inject constructor() :
    BaseViewModelFragment<FragmentFiltersBinding, ViewData, FilterViewModel>() {

    override val viewModel: FilterViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentFiltersBinding
        get() = FragmentFiltersBinding::inflate

    override fun initializeView() {
        setupAdapter()
        log.d("viewModel link $viewModel")
    }

    override fun render(data: ViewData) {
        usersAdapter.submitList(data.usersUi)
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

}