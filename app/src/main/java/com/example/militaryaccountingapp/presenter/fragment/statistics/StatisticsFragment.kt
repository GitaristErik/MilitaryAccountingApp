package com.example.militaryaccountingapp.presenter.fragment.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentStatisticsBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ChartType
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ViewData
import com.google.android.material.button.MaterialButton
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment :
    BaseViewModelFragment<FragmentStatisticsBinding, ViewData, StatisticsViewModel>() {

    override val viewModel: StatisticsViewModel by viewModels()

    private val filterViewModel: FilterViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStatisticsBinding
        get() = FragmentStatisticsBinding::inflate

    override fun initializeView() {
        setupFilterFragment()
        setupActionBar()
        setupCountButtons()
    }

    override fun render(data: ViewData) {
        log.d("render")
    }


    private fun renderFilter(data: FilterViewModel.ViewData) {
        log.d("render")
        if (data.isFiltersSelected) {
            binding.countGroup.isEnabled = true
        } else {
            binding.countGroup.clearChecked()
            binding.countGroup.isEnabled = false
        }
    }

    override fun observeData() {
        super.observeData()
        viewLifecycleOwner.lifecycleScope.launch {
            filterViewModel.data
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collect { renderFilter(it) }
        }
    }

    private fun setupFilterFragment() {
        childFragmentManager.beginTransaction()
            .add(R.id.filters, FilterFragment::class.java, null)
            .commit()
    }

    private fun setupActionBar() {
        binding.appBarLayout.statusBarForeground = MaterialShapeDrawable().apply {
            setTint(resources.getColor(R.color.md_surface, null))
        }
    }

    private fun setupCountButtons() {
        binding.countGroup.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            toggleButton.findViewById<MaterialButton>(checkedId).icon =
                if (isChecked) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_check_24dp
                ) else null

            if (isChecked) when (checkedId) {
                R.id.button_users -> viewModel.changeChartType(ChartType.Users::class)
                R.id.button_items -> viewModel.changeChartType(ChartType.Count::class)
                else -> viewModel.changeChartType(null)
            } else if (!binding.buttonItems.isChecked &&
                !binding.buttonUsers.isChecked
            ) viewModel.changeChartType(null)
        }
    }
}