package com.example.militaryaccountingapp.presenter.fragment.history

import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.history.HistoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.TimelineUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(): BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
        fetch()
    }

    private fun fetch() {
        _data.update { viewData ->
            viewData.copy(
                timelineItems = List(10) {
                    TimelineUi(
                        date = "12.12.12",
                        title = "title",
                        value = "${it + 1}",
                        operation = ActionType.INCREASE_COUNT,
                        location = "location",
                        name = "name"
                    )
                }
            )
        }
    }

     data class ViewData(
         val timelineItems: List<TimelineUi> = emptyList(),
    )

}