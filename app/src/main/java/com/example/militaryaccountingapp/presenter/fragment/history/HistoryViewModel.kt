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
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class
HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val timelineItems: List<TimelineUi> = emptyList(),
    )

    init {
        Timber.d("init")
//        forceLoadHistory(100)
    }

    var checkedFilters = emptySet<ActionType>()

    fun loadHistory(
        limit: Long = 100,
        filters: Set<ActionType> = emptySet(),
        page: Int = 0
    ) {
        if (checkedFilters == filters) return
        else forceLoadHistory(limit, filters, page)
    }

    fun forceLoadHistory(
        limit: Long = 100,
        filters: Set<ActionType> = emptySet(),
        page: Int = 0,
    ) {
        safeRunJobWithLoading(Dispatchers.IO) {
//            delay(DELAY)
            val i = resultWrapper(
                getHistoryUseCase(
                    limit = limit,
                    filters = filters
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


    companion object {
        private const val DELAY = 400L
    }
}