package com.example.militaryaccountingapp.presenter.fragment.statistics

import android.view.LayoutInflater
import android.view.View
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
import com.example.militaryaccountingapp.presenter.shared.chart.ChartBubbleBinder
import com.example.militaryaccountingapp.presenter.shared.chart.ChartPieBinder
import com.example.militaryaccountingapp.presenter.shared.chart.ListenerValueSelected
import com.example.militaryaccountingapp.presenter.shared.chart.TestData
import com.github.mikephil.charting.data.PieEntry
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
        when (data.chartType) {
            is ChartType.Count -> renderCountChartItems()
            is ChartType.Users -> renderCountChartUsers()
            else -> setupCountChart()
        }
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

    private val countChartUsers by lazy {
        ChartPieBinder(requireContext(), binding.countChartViewUsers, ListenerValueSelected).apply {
            bind("All Items")
        }
    }

    private val countChartItems by lazy {
        ChartBubbleBinder(
            requireContext(),
            binding.countChartViewItems,
            ListenerValueSelected
        ).apply {
            bind()
        }
    }

    private fun setupCountChart() {
        binding.countChartViewUsers.visibility = View.INVISIBLE
        binding.countChartViewUsers.visibility = View.INVISIBLE
    }

    private fun renderCountChartUsers(
        entries: List<PieEntry> = ChartPieBinder.getDatasetEntries(3, 10f)
    ) {
        binding.countChartViewUsers.visibility = View.VISIBLE
        binding.countChartViewItems.visibility = View.INVISIBLE
        countChartUsers.setData(entries)
    }

    private fun renderCountChartItems(
        entries: List<TestData> = TestData.generateTestData()
    ) {
        binding.countChartViewUsers.visibility = View.INVISIBLE
        binding.countChartViewItems.visibility = View.VISIBLE
        countChartItems.setData(
            countChartItems.generateData(entries)
        )
    }
}