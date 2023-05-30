package com.example.militaryaccountingapp.presenter.fragment.details

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
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
        val qrData = QrData.Text(data)

        val options = createQrVectorOptions {
            padding = .125f
            background {
                drawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_qr_code
                )
            }
            colors {
                dark = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.md_secondaryContainer)
                )
                ball = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.md_primaryContainer)
                )
                frame = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.md_primaryContainer)
                )
            }
            shapes {
                darkPixel = QrVectorPixelShape
                    .Circle(0.85f)
                ball = QrVectorBallShape
                    .Circle(.5f)
                frame = QrVectorFrameShape
                    .RoundCorners(.5f, .75f)
            }
        }
        val drawable: Drawable = QrCodeDrawable(qrData, options)
        binding.qrImage.setImageDrawable(drawable)
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