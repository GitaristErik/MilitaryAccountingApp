package com.example.militaryaccountingapp.presenter.fragment.categories

import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.categories.CategoriesViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.CategoryUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.OrderBy
import com.example.militaryaccountingapp.presenter.utils.common.constant.SortType
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val getCurrentUserUseCase: CurrentUserUseCase
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val viewType: ViewType = ViewType.LIST,
        val orderBy: OrderBy = OrderBy.DESCENDING,
        val sortType: SortType = SortType.NAME,
        val mainData: Results<List<CategoryUi>> = Results.Loading(emptyList()),
        val cache: List<CategoryUi> = emptyList(),
        val page: Long = 1L,
    )

    val viewType: ViewType get() = data.value.viewType
    val orderBy: OrderBy get() = data.value.orderBy
    val sortType: SortType get() = data.value.sortType

    var parentId: String? = null

    init {
        fetch()
    }

    fun reload() {
        Timber.d("reload")
//        _data.update { it.copy(page = 1L) }
        fetch()
    }


    private fun fetch() {
        safeRunJobWithLoading(Dispatchers.IO) {
            val currentUser = getCurrentUserUseCase() ?: return@safeRunJobWithLoading

            val res: Results<List<CategoryUi>> = resultWrapper(
                dataRepository.getDataByParent(
                    Category::class.java,
                    parentId = parentId ?: currentUser.rootCategoryId,
                    userId = currentUser.id,
                    sortType = mapSortType(),
                    isAscending = orderBy == OrderBy.ASCENDING
                )
            ) { dataListMap ->
                val usersIds = dataListMap.values.flatten().map { it.userId }.distinct()
                val avatarsAllUsers = userRepository.getUsersAvatars(usersIds)

                val categories = dataListMap.map { (data, permissions) ->
                    CategoryUi(
                        id = data.id,
                        name = data.name,
                        description = data.description,
                        imageUrl = data.imagesUrls.firstOrNull() ?: "",
                        usersAvatars = permissions.mapNotNull {
                            if (it.userId == currentUser.id) null
                            else avatarsAllUsers[it.userId] ?: ""
                        },
                        qrCode = data.barCodes.firstOrNull()?.code,
                        parentId = data.parentId,
                    )
                }
                Results.Success(categories)
            }
            _data.update { it.copy(mainData = res) }

            if (res is Results.Success) {
                calculateStatistics(res.data)
            }
        }
    }

    private fun calculateStatistics(
        categories: List<CategoryUi>
    ) = viewModelScope.launch(Dispatchers.IO) {
        val newCategories = categories.map { category ->
            val itemsCountRes = itemRepository.getItemsCount(category.id)
            val itemsCount = (itemsCountRes as? Results.Success)?.data?.toInt() ?: 0
            val categoriesCountRes = categoryRepository.getCategoriesCount(category.id)
            val categoriesCount = (categoriesCountRes as? Results.Success)?.data?.toInt() ?: 0
//            val allCount = itemsCount + categoriesCount

            category.copy(
                itemsCount = itemsCount,
                categoriesCount = categoriesCount,
//                allCount = allCount
            )
        }
        _data.update { it.copy(mainData = Results.Success(newCategories)) }
    }


    private fun mapSortType(): DataRepository.SortFilter {
        return when (sortType) {
            SortType.NAME -> DataRepository.SortFilter.NAME
            SortType.DATE_CREATED -> DataRepository.SortFilter.DATE_CREATED
            SortType.DATE_UPDATED -> DataRepository.SortFilter.DATE_UPDATED
            SortType.DESCRIPTION -> DataRepository.SortFilter.DESCRIPTION
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