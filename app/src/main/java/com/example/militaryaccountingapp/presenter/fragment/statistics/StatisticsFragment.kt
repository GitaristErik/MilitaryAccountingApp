package com.example.militaryaccountingapp.presenter.fragment.statistics

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentStatisticsBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ChartType
import com.example.militaryaccountingapp.presenter.fragment.statistics.StatisticsViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.chart.count.ChartPieBinder
import com.example.militaryaccountingapp.presenter.shared.chart.count.ListenerValueSelected
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChart
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import com.example.militaryaccountingapp.presenter.shared.chart.items.ChartBubbleBinder
import com.example.militaryaccountingapp.presenter.shared.chart.items.TestData
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.button.MaterialButton
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
//        setupFilterFragment()
        setupCountButtons()
        setupHistorySpinner()
        observeCustomData2()
        log.d("viewModel link $filterViewModel")
    }

    override fun render(data: ViewData) {
        log.d("render")
        when (data.countChartType) {
            is ChartType.Count -> renderCountChartItems()
            is ChartType.Users -> data.countChartData?.let(::renderCountChartUsers)
            else -> setupCountChart()
        }
        renderHistoryChart(data.historyChartType)
    }


    private fun renderFilter(data: FilterViewModel.ViewData) {
        log.d("render")
        if (data.isFiltersSelected) {
            binding.countGroup.isEnabled = true
            viewModel.usersChartData = data.usersUi
                .filter { it.id in data.selectedUsersId }
                .map { PieEntry(it.count.toFloat(), it.name, it.count.toString()) }
            viewModel.changeHistoryChartType(DayData::class)
        } else {
            binding.countGroup.clearChecked()
            binding.countGroup.isEnabled = false
            viewModel.changeHistoryChartType(null)
        }
    }

    /*    override suspend fun observeCustomData() {
            filterViewModel.data
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { renderFilter(it) }
        }*/

    private fun observeCustomData2() {
        viewLifecycleOwner.lifecycleScope.launch {
            filterViewModel.data
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { renderFilter(it) }
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
                R.id.button_users -> viewModel.changeCountChartType(ChartType.Users::class)
                R.id.button_items -> viewModel.changeCountChartType(ChartType.Count::class)
                else -> viewModel.changeCountChartType(null)
            } else if (!binding.buttonItems.isChecked &&
                !binding.buttonUsers.isChecked
            ) viewModel.changeCountChartType(null)
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
        entries: List<PieEntry>// = ChartPieBinder.getDatasetEntries(3, 10f)
    ) {
        binding.countChartViewUsers.visibility = View.VISIBLE
        binding.countChartViewItems.visibility = View.INVISIBLE
        countChartUsers.setData(entries)
    }

    private fun renderCountChartItems(
        entries: List<TestData> = TestData.generateTestData()
    ) {
        log.d("renderCountChartItems $entries")
        binding.countChartViewUsers.visibility = View.INVISIBLE
        binding.countChartViewItems.visibility = View.VISIBLE
        countChartItems.setData(
            countChartItems.generateData(entries)
        )
    }

    private fun renderHistoryChart(data: ChartData? = null) {
        if (chartData == data) return
        else chartData = data

        if (data == null) {
//            setHistoryChartNoDataText()
        } else {
//            historyChart.displayData(data)
        }
    }


    private val historyChart: HistoryChart by lazy {
        HistoryChart(requireContext(), binding.historyChartView).apply {
            setupChart()
        }
    }

    private fun setHistoryChartNoDataText() = binding.historyChartView.apply {
        getPaint(Chart.PAINT_INFO).color = Color.WHITE
        setNoDataText(getString(R.string.statistics_history_loading))
        invalidate()
    }


    private fun setupHistorySpinner() {}

    private fun setupHistorySpinner2() {
        binding.historySpinner.apply {
            this.adapter = ArrayAdapter.createFromResource(
                this@StatisticsFragment.requireContext(),
                R.array.history_spinner, android.R.layout.simple_spinner_item
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
//            setSelection(3)
            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        when (position) {
                            0 -> viewModel.changeHistoryChartType(DayData::class)
                            1 -> viewModel.changeHistoryChartType(WeekData::class)
                            2 -> viewModel.changeHistoryChartType(MonthData::class)
                            else -> viewModel.changeHistoryChartType(null)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    companion object {
        private var chartData: ChartData? = null
    }
}