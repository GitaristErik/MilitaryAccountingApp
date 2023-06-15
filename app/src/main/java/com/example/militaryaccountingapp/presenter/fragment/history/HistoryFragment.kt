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
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHistoryBinding
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel
import com.example.militaryaccountingapp.presenter.fragment.history.HistoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.ScrollableTopScreen
import com.example.militaryaccountingapp.presenter.shared.adapter.TimeLineAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.TimelineDecorator
import com.google.android.material.chip.Chip
import com.lriccardo.timelineview.TimelineView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment :
    BaseViewModelFragment<FragmentHistoryBinding, ViewData, HistoryViewModel>(),
    ScrollableTopScreen {

    override val viewModel: HistoryViewModel by viewModels()

    private val filterViewModel: FilterViewModel by activityViewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHistoryBinding
        get() = FragmentHistoryBinding::inflate


    private val pageLimit = lazy {
        requireContext().resources.getInteger(R.integer.history_page_items_count)
    }

    override fun initializeView() {
        setupTimeline()
        observeCustomData2()
        setupChips()
    }

    override fun render(data: ViewData) {
        log.d("render timeline ${data.timelineItems}")
        timelineAdapter.submitList(data.timelineItems)
    }


    private fun renderFilter(data: FilterViewModel.ViewData) {
        log.d("render")
        if (data.isFiltersSelected) {
        } else {
        }
    }

    private fun observeCustomData2() {
        viewLifecycleOwner.lifecycleScope.launch {
            filterViewModel.data
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { renderFilter(it) }
        }
    }

    private fun setupTimeline() {
        timelineAdapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY

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
                    padding = 0f,
                    indicatorSize = 28f,
                    checkedIndicatorSize = 12f,
                    lineStyle = TimelineView.LineStyle.Dashed,
                    lineWidth = 16f,
                    lineDashGap = 16f,
//                    lineDashLength = 32f,
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
        viewModel.checkedFilters.forEach { actionType ->
            filterMapper(actionType)?.let { binding.chipGroup.check(it) }
        }
        viewModel.forceLoadHistory(limit = pageLimit.value.toLong())

        binding.chipGroup.children.forEach {
            (it as? Chip)?.setOnCheckedChangeListener { compoundButton, isChecked ->
                (compoundButton as? Chip)?.apply {
                    isChipIconVisible = !isChecked
                    log.d("chip: $text, isChecked: $isChecked")
                }
            }
        }

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            log.d("checkedIds: $checkedIds")
            viewModel.loadHistory(
                page = 0, // TODO add pagination
                filters = checkedIds.mapNotNull(::chipMapper).toSet(),
                limit = pageLimit.value.toLong()
            )
        }
    }

    private fun chipMapper(chipId: Int): ActionType? = when (chipId) {
        R.id.chip_add -> ActionType.INCREASE_COUNT
        R.id.chip_decrease -> ActionType.DECREASE_COUNT
        R.id.chip_share -> ActionType.SHARE
        R.id.chip_unshare -> ActionType.UNSHARE
        R.id.chip_create -> ActionType.CREATE
        R.id.chip_delete -> ActionType.DELETE
        R.id.chip_restore -> ActionType.RESTORE
        else -> null
    }

    private fun filterMapper(chipId: ActionType): Int? = when (chipId) {
        ActionType.INCREASE_COUNT -> R.id.chip_add
        ActionType.DECREASE_COUNT -> R.id.chip_decrease
        ActionType.SHARE -> R.id.chip_share
        ActionType.UNSHARE -> R.id.chip_unshare
        ActionType.CREATE -> R.id.chip_create
        ActionType.DELETE -> R.id.chip_delete
        ActionType.RESTORE -> R.id.chip_restore
        else -> null
    }
}