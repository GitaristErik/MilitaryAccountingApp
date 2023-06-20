package com.example.militaryaccountingapp.presenter.fragment.history

import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.usecase.GetHistoryUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.history.HistoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.TimelineUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.FilterDate
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class
HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val timelineItems: List<TimelineUi> = emptyList(),
        val filterDate: FilterDate = FilterDate.PickDay(Date().time),
    )

    val filterDate: FilterDate get() = _data.value.filterDate


    init {
        Timber.d("init")
//        forceLoadHistory(100)
    }

    var checkedFilters: Set<ActionType>? = null

    private var usersIds: List<String>? = null
    private var itemsIds: List<String>? = null
    private var categoriesIds: List<String>? = null


    fun loadHistory(
        limit: Long = 25,
        usersIds: List<String>? = this.usersIds,
        categoriesIds: List<String>? = this.categoriesIds,
        itemsIds: List<String>? = this.itemsIds,
        filters: Set<ActionType> = emptySet(),
        page: Int = 0
    ) {
        if (checkedFilters == filters || usersIds == null || categoriesIds == null || itemsIds == null)
            return
        else forceLoadHistory(
            limit = limit,
            filters = filters,
            usersIds = usersIds,
            categoriesIds = categoriesIds,
            itemsIds = itemsIds,
            page = page
        )
    }

    private fun forceLoadHistory(
        limit: Long = 25,
        usersIds: List<String>? = null,
        categoriesIds: List<String>? = null,
        itemsIds: List<String>? = null,
        filters: Set<ActionType> = emptySet(),
        page: Int = 0,
    ) {
        safeRunJobWithLoading(Dispatchers.IO) {
//            delay(DELAY)
            this@HistoryViewModel.usersIds = usersIds
            this@HistoryViewModel.categoriesIds = categoriesIds
            this@HistoryViewModel.itemsIds = itemsIds

            log.d("forceLoadHistory with limit = $limit, filters = $filters, page = $page || date = $filterDate")
            val i = resultWrapper(
                getHistoryUseCase(
                    limit = limit,
                    usersIds = usersIds,
//                    categoriesIds = categoriesIds,
//                    itemsIds = itemsIds,
                    filters = filters,
                    dateStart = when (filterDate) {
                        is FilterDate.PickDay -> filterDate.date
                        is FilterDate.Range -> (filterDate as FilterDate.Range).startDate
                        is FilterDate.After -> (filterDate as FilterDate.After).date
                        is FilterDate.Before -> null
                    }?.let { it - 24 * 60 * 60 * 1000 },
                    dateEnd = when (filterDate) {
                        is FilterDate.PickDay -> filterDate.date + 24 * 60 * 60 * 1000
                        is FilterDate.Range -> (filterDate as FilterDate.Range).date
                        is FilterDate.After -> null
                        is FilterDate.Before -> (filterDate as FilterDate.Before).date
                    },
                )
            ) {
                checkedFilters = filters
                log.d("fetch history with limit = $limit, filters = $filters, page = $page ||| items: $it")
                _data.update { viewData ->
                    viewData.copy(
                        timelineItems = it.map(::mapToTimeline)
                    )
                }
                Results.Success("All data loaded")
            }
            log.d("forceLoadHistory res: $i")
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
    private fun mapToTimeline(item: Triple<Action, Data?, User>): TimelineUi {
        val tempData: Data? = item.second ?: item.first.value.let {
            when (it) {
                is Data -> it
                is Results.Success<*> -> (it.data as? Data)
                else -> null
            }
        }

        val value = if (item.first.action in
            listOf(ActionType.INCREASE_COUNT, ActionType.DECREASE_COUNT)
        ) item.first.value?.toString() ?: "" else ""

        return TimelineUi(
            title = tempData?.name ?: "",
            location = tempData?.allParentNames?.joinToString("\n") ?: "",
            date = item.first.timestamp.asFormattedDateString(),
            value = value,
            operation = item.first.action,
            name = item.third.name,
            userIcon = item.third.imageUrl,
        )
    }

    fun changeDateSelection(date: FilterDate) {
        _data.update {
            it.copy(
                filterDate = date,
            )
        }
        loadHistory()
    }


    companion object {
        private const val DELAY = 400L
    }
}