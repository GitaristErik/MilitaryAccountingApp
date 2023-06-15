package com.example.militaryaccountingapp.presenter.fragment.details

import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.data.Barcode
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.details.DetailsItemViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.LastChangedUi
import com.example.militaryaccountingapp.presenter.model.UserSearchUi
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChartItem
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlin.reflect.KClass

@HiltViewModel
class DetailsItemViewModel @Inject constructor(
    private val itemsRepository: ItemRepository,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository,
    private val currentUserUseCase: CurrentUserUseCase,
) : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    data class ViewData(
        val historyChartType: ChartData? = null,
        val codes: Set<Barcode> = emptySet(),
        val item: Results<Item> = Results.Loading(null),
        val lastChanged: Results<LastChangedUi> = Results.Loading(null),
        val isDeleted: Results<Void?> = Results.Loading(null),
        val users: Results<List<UserSearchUi>> = Results.Loading(),
    )

    private val _dataImages: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val dataImages: StateFlow<Set<String>> = _dataImages.asStateFlow()

    fun sendArguments(
        id: String,
        name: String,
        parentIds: Array<String>?,
        imageUrlIds: Array<String>?,
        description: String,
        count: Int
    ) {
        _data.update {
            it.copy(
                item = Results.Loading(
                    Item(
                        id = id,
                        name = name,
                        allParentIds = parentIds?.toList() ?: emptyList(),
                        imagesUrls = imageUrlIds?.toList() ?: emptyList(),
                        description = description,
                        count = count
                    )
                )
            )
        }
        fetch(id)
    }


    private fun fetchUsersNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserUseCase()?.let { user ->
                val res = resultWrapper(
                    userRepository.getUsers(user.usersInNetwork)
                ) {
                    Results.Success(mapToUserSearchUi(it))
                }
                log.e("fetchUsersNetwork: $res")
                _data.update { viewData -> viewData.copy(users = res) }
            }
        }
    }

    private fun mapToUserSearchUi(it: List<User>): List<UserSearchUi> = it.map { user ->
        UserSearchUi(
            id = user.id,
            fullName = user.fullName,
            name = user.name,
            rank = user.rank,
            imageUrl = user.imageUrl,
        )
    }


    private fun fetch(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = itemsRepository.getItem(id)
            val barcodes = (item as? Results.Success)
                ?.data?.barCodes?.toSet() ?: emptySet()
            _data.update {
                it.copy(item = item, codes = barcodes)
            }
            getLastChanged(id)
            fetchUsersNetwork()
        }
    }

    private suspend fun getLastChanged(itemId: String) {
//        safeRunJobWithLoading(Dispatchers.IO) {
        val res: Results<LastChangedUi> = resultWrapper(
            historyRepository.getLastAction(
                id = itemId,
                element = HistoryRepository.ActionElement.ITEM
            )
        ) {
            resultWrapper(
                userRepository.getUser(it.userId)
            ) { user ->
                Results.Success(
                    LastChangedUi(
                        timestamp = it.timestamp,
                        name = user.name,
                        rank = user.rank,
                        avatarUrl = user.imageUrl
                    )
                )
            }
        }

        _data.update { it.copy(lastChanged = res) }
//        }
    }

    fun deleteItem() {
        safeRunJobWithLoading(Dispatchers.IO) {
            (_data.value.item as? Results.Success)?.let { success ->
                val res = itemsRepository.deleteItem(success.data.id)
                _data.update {
                    it.copy(isDeleted = res)
                }
            }
        }
    }


    fun changeHistoryChartType(chartType: KClass<out ChartData>?) {
        safeRunJob(Dispatchers.Default) {
            val data = List(10) {
                HistoryChartItem(
                    LocalDate.of(2023, 5, it + 1).toString(),
                    (Math.random() * 100).toInt()
                )
            }

            val chart = when (chartType) {
                DayData::class -> DayData(data)
                WeekData::class -> WeekData(mapToWeeks(data))
                MonthData::class -> MonthData(data)
                else -> null
            }?.apply { generate() }

            _data.update { it.copy(historyChartType = chart) }
        }
    }

    private fun mapToWeeks(dates: List<HistoryChartItem>): List<HistoryChartItem> =
        dates.groupBy {
            it.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        }.map { (sunday, dates) ->
            val items = dates.sumOf { it.items }
            HistoryChartItem(sunday.toString(), items)
        }.sortedBy { it.date }

    fun handleShowedDeleted() {
        _data.update { it.copy(isDeleted = Results.Loading(null)) }
    }

    fun setRules(
        userId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    ) {
        val elementId = (_data.value.item as? Results.Success)?.data?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            itemsRepository.changeRules(
                elementId,
                userId,
                canRead,
                canEdit,
                canShareRead,
                canShareEdit
            )
        }
    }

    fun loadUserPermission(
        userId: String,
        handleOnLoad: (Results<UserPermission?>, String) -> Unit
    ) {
        val elementId = (_data.value.item as? Results.Success)?.data?.id ?: return
        viewModelScope.launch {
            handleOnLoad(
                permissionRepository.getPermission(
                    itemId = elementId,
                    userId = userId
                ), elementId
            )
        }
    }

}