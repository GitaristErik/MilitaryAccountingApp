package com.example.militaryaccountingapp.presenter.fragment.history

import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.usecase.GetHistoryUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.history.HistoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.TimelineUi
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val timelineItems: List<TimelineUi> = emptyList(),
    )

    init {
        Timber.d("init")
    }

    private var checkedFilters = emptySet<ActionType>()

    fun loadHistory(
        limit: Int = -1,
        filters: Set<ActionType> = emptySet(),
        page: Int = 0,
    ) {
        safeRunJob(Dispatchers.IO) {
            if (checkedFilters == filters) return@safeRunJob
            delay(DELAY)
            val historyItems = getHistoryUseCase(
                limit = limit,
                filters = filters,
                page = page,
            )
            checkedFilters = filters
            log.d("fetch history with limit = $limit, filters = $filters, page = $page")
            _data.update { viewData ->
                viewData.copy(
                    timelineItems = historyItems.mapNotNull(::mapToTimeline)
                )
            }
        }
    }


    /**
     * Map data from repository to timeline item
     * @param item triple of action, data and user
     * @return timeline item or null if data is null
     * @see TimelineUi
     * @see Action
     * @see Data
     * @see User
     */
    private fun mapToTimeline(item: Triple<Action, Data?, User>): TimelineUi? =
        if (item.second != null &&
            item.first.categoryId == item.second?.id &&
            item.first.itemId == item.second?.id
        ) TimelineUi(
            title = item.second!!.name,
            location = item.second?.allParentIds?.joinToString("/") ?: "",
            date = item.first.timestamp.asFormattedDateString(),
            value = item.first.value.toString(),
            operation = item.first.action,
            name = item.third.name,
            userIcon = item.third.imageUrl,
        ) else null


    companion object {
        private const val DELAY = 400L
    }
}