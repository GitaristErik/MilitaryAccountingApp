package com.example.militaryaccountingapp.presenter.fragment.categories

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.categories.CategoriesViewModel.ViewData
import com.example.militaryaccountingapp.presenter.utils.common.constant.OrderBy
import com.example.militaryaccountingapp.presenter.utils.common.constant.SortType
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val viewType: ViewType = ViewType.GRID,
        val orderBy: OrderBy = OrderBy.DESCENDING,
        val sortType: SortType = SortType.NAME,
        val categories: List<String> = emptyList(),
    )

    val viewType: ViewType get() = data.value.viewType
    val orderBy: OrderBy get() = data.value.orderBy
    val sortType: SortType get() = data.value.sortType


    init {
        fetch()
    }

    private fun reload() {
        Timber.d("reload")
//        _data.update { it.copy(page = 1L) }
        fetch()
    }

    private fun fetch() {
        safeRunJob(Dispatchers.Default) {
            val categories = List(20) { (it + 1).toString() }
            delay(500)
            _data.update {
                it.copy(
                    categories = if (sortType == SortType.DATE) categories
                    else categories.asReversed()
                )
            }
        }
    }

    fun changeSortType(sortType: SortType) {
        _data.update { it.copy(sortType = sortType) }
        reload()
    }

    fun changeOrderBy() {
        val newOrderBy = if (data.value.orderBy == OrderBy.ASCENDING)
            OrderBy.DESCENDING
        else
            OrderBy.ASCENDING
        _data.update { it.copy(orderBy = newOrderBy) }
        reload()
    }

    fun changeViewType() {
        val newViewType = if (data.value.viewType == ViewType.GRID)
            ViewType.LIST
        else
            ViewType.GRID
        _data.update { it.copy(viewType = newViewType) }
    }
}