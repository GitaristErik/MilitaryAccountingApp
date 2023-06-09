package com.example.militaryaccountingapp.presenter.fragment.details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentCategoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.details.DetailsViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChart
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import com.example.militaryaccountingapp.presenter.utils.ui.ext.initAsQrFull
import com.github.mikephil.charting.charts.Chart
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsCategoryFragment :
    BaseViewModelFragment<FragmentCategoryBinding, ViewData, DetailsViewModel>() {

    override val viewModel: DetailsViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoryBinding
        get() = FragmentCategoryBinding::inflate

    override fun initializeView() {
        setupActionBar()
        setupQrCode()
        setupHistorySpinner()
    }

    override fun render(data: ViewData) {
        log.d("render")
        renderHistoryChart(data.historyChartType)
    }

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
//            setSupportActionBar(binding.toolbar)
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit -> {
                            // Handle edit text press
                            true
                        }

                        R.id.delete -> {
                            // Handle more item (inside overflow menu) press
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    private fun setupQrCode() {
        val data = "This is the test data for qr code"
        binding.qrImage.initAsQrFull(data)
    }


    private fun renderHistoryChart(data: ChartData? = null) {
        if (chartData == data) return
        else chartData = data

        if (data == null) {
            setHistoryChartNoDataText()
        } else {
            historyChart.displayData(data)
        }
    }


    private val historyChart: HistoryChart by lazy {
        HistoryChart(requireContext(), binding.historyChart).apply {
            setupChart()
        }
    }

    private fun setHistoryChartNoDataText() = binding.historyChart.apply {
        getPaint(Chart.PAINT_INFO).color = Color.WHITE
        setNoDataText(getString(R.string.statistics_history_loading))
        invalidate()
    }

    private fun setupHistorySpinner() {
        binding.historySpinner.apply {
            this.adapter = ArrayAdapter.createFromResource(
                this@DetailsCategoryFragment.requireContext(),
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