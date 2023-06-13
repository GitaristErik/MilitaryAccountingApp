package com.example.militaryaccountingapp.presenter.fragment.items

import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.items.ItemsViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.ItemUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.OrderBy
import com.example.militaryaccountingapp.presenter.utils.common.constant.SortType
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository,
//    private val codeRepository: BarcodeRepository,
    private val getCurrentUserUseCase: CurrentUserUseCase
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val viewType: ViewType = ViewType.LIST,
        val orderBy: OrderBy = OrderBy.DESCENDING,
        val sortType: SortType = SortType.NAME,
        val mainData: Results<List<ItemUi>> = Results.Loading(emptyList()),
        val cache: List<ItemUi> = emptyList(),
        val page: Long = 1L,
    )

    val viewType: ViewType get() = data.value.viewType
    val orderBy: OrderBy get() = data.value.orderBy
    val sortType: SortType get() = data.value.sortType

    var parentId: String? = null

    fun reload() {
        Timber.d("reload")
        fetch()
    }

    private fun fetch() {
        safeRunJobWithLoading(Dispatchers.IO) {
            val currentUser = getCurrentUserUseCase() ?: return@safeRunJobWithLoading

            val res: Results<List<ItemUi>> = resultWrapper(
                dataRepository.getDataByParent(
                    Item::class.java,
                    parentId = parentId ?: currentUser.rootCategoryId,
                    userId = currentUser.id,
                    sortType = mapSortType(),
                    isAscending = orderBy == OrderBy.ASCENDING
                )
            ) { dataListMap ->
                val usersIds = dataListMap.values.flatten().map { it.userId }.distinct()
                val avatarsAllUsers = userRepository.getUsersAvatars(usersIds)

                val items = dataListMap.map { (data, permissions) ->
                    ItemUi(
                        id = data.id,
                        name = data.name,
                        description = data.description,
                        count = (data as Item).count,
                        imageUrl = data.imagesUrls.firstOrNull() ?: "",
                        usersAvatars = permissions.mapNotNull {
                            if (it.userId == currentUser.id) null
                            else avatarsAllUsers[it.userId] ?: ""
                        },
                        qrCode = data.barCodes.firstOrNull()?.code,
                        parentId = data.parentId,
                    )
                }
                Results.Success(items)
            }
            log.e("items frergjnrev $res")
            _data.update { it.copy(mainData = res) }
        }
    }
    /*

        private suspend fun getCode(id: String): String? = codeRepository.getCode(id).let {
            if (it !is Results.Success) null
            else it.data.code
        }
    */


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