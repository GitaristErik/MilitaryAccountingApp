package com.example.militaryaccountingapp.presenter.fragment.categories

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.categories.CategoriesViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.CategoryUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.OrderBy
import com.example.militaryaccountingapp.presenter.utils.common.constant.SortType
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.lang.Math.random
import javax.inject.Inject
import kotlin.math.nextDown

@HiltViewModel
class CategoriesViewModel @Inject constructor(
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val viewType: ViewType = ViewType.GRID,
        val orderBy: OrderBy = OrderBy.DESCENDING,
        val sortType: SortType = SortType.NAME,
        val categories: List<CategoryUi> = emptyList(),
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
            val categories = getCategories()
            _data.update {
                it.copy(categories = categories)
            }
        }
    }

    private fun getCategories(): List<CategoryUi> {
        val list = mutableListOf<CategoryUi>()
        val rand: (Int, Int) -> Int = { x1, x2 ->
            val f = random() / 1.0.nextDown()
            val x = x1 * (1.0 - f) + x2 * f
            x.toInt()
        }

        for (i in 1..8) {
            list.add(
                CategoryUi(
                    id = i,
                    name = "Category #${i}",
                    description = "Description for category #${i}",
                    itemsCount = rand(0, 10),
                    nestedCount = rand(0, 4),
                    allCount = rand(0, 20),
                    imageUrl = "",
                    color = "#FFFFFF",
                    parentId = null,
                )
            )
        }
        return list
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