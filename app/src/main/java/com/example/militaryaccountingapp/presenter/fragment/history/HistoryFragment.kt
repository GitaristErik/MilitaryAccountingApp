package com.example.militaryaccountingapp.presenter.fragment.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHistoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel
import com.example.militaryaccountingapp.presenter.shared.ScrollableTopScreen
import com.example.militaryaccountingapp.presenter.shared.adapter.TimeLineAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.TimelineDecorator
import com.google.android.material.chip.Chip
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment :
    BaseViewModelFragment<FragmentHistoryBinding, HistoryViewModel.ViewData, HistoryViewModel>(),
    ScrollableTopScreen {

    override val viewModel: HistoryViewModel by viewModels()

    private val filterViewModel: FilterViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHistoryBinding
        get() = FragmentHistoryBinding::inflate

    override fun initializeView() {
        setupActionBar()
        setupFilterFragment()
        setupTimeline()
        setupChips()
    }

    override fun render(data: HistoryViewModel.ViewData) {
        log.d("render")
//        setChips(data.chips)
        timelineAdapter.submitList(data.timelineItems)
    }

    private fun renderFilter(data: FilterViewModel.ViewData) {
        log.d("render")
        if (data.isFiltersSelected) {
        } else {
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

    private fun setupTimeline() {
        timelineAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.rvTimeline.let {
            it.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            it.adapter = concatAdapter

            val colorPrimary = ContextCompat.getColor(requireContext(), R.color.md_primary)
            it.addItemDecoration(
                TimelineDecorator(
                    position = TimelineDecorator.Position.Left,
                    indicatorColor = colorPrimary,
                    lineColor = colorPrimary,
//                    lineStyle = TimelineView.LineStyle.Normal,
                    padding = 0f,
                    indicatorSize = 20f,
                    checkedIndicatorSize = 20f,
                    lineWidth = 12f,
                    linePadding = 0f
                )
            )
        }
    }

    private val timelineAdapter: TimeLineAdapter by lazy {
        TimeLineAdapter { _, _ -> }
    }

    private val concatAdapter by lazy {
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.NO_STABLE_IDS)
            .build()

        ConcatAdapter(
            concatAdapterConfig,
            timelineAdapter
        )
    }

    override fun scrollToTop() {
        binding.rvTimeline.scrollToPosition(0)
    }

    private fun setupChips() {
//        log.d(binding.chipGroup.children.joinToString(" "))
        binding.chipGroup.children.forEach {
            (it as? Chip)?.setOnCheckedChangeListener { compoundButton, isChecked ->
                (compoundButton as? Chip)?.apply {
                    isChipIconVisible = !isChecked
                    log.d("chip: $text, isChecked: $isChecked")
                }
            }
        }
    }

    /*
        private fun setChips(chips: List<ChipData>?) = binding.run {
            if (chips == null) {
                chipGroup.removeAllViews()
                return@run
            }
            chips.forEach { chipName ->
                chipGroup.addView(
                    Chip(
                        context, null,
                        com.google.android.material.R.style.Widget_Material3_Chip_Filter_Elevated
                    ).apply {
                        text = chipName
                        isCheckable = true
                        isChecked = true
                        id =
                        shapeAppearanceModel = context.resources.getDimension(R.dimen.corner_chip).let {
                            ShapeAppearanceModel().withCornerSize(it)
                        }
                    })
            }
        }*/
}