package com.example.militaryaccountingapp.presenter.fragment.details

import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.data.Barcode
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.details.DetailsCategoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.LastChangedUi
import com.example.militaryaccountingapp.presenter.model.UserSearchUi
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChartItem
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import com.github.mikephil.charting.data.PieEntry
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
class DetailsCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val permissionRepository: PermissionRepository
) : BaseViewModel<ViewData>(ViewData()) {

    init {
        Timber.d("init")
    }

    data class ViewData(
        val historyChartType: ChartData? = null,
        val codes: Set<Barcode> = emptySet(),
        val lastChanged: Results<LastChangedUi> = Results.Loading(null),
        val isDeleted: Results<Unit> = Results.Loading(null),
        val users: Results<List<UserSearchUi>> = Results.Loading(),
        val chartData: List<PieEntry>? = null
    )

    private suspend fun mapUsersToChart(users: List<User>): List<PieEntry> {
        return users.map {
            val countItems = (permissionRepository.getReadCount(
                destinationUserId = it.id,
                grantUserId = currentUser!!.id
            ) as? Results.Success)?.data?.toInt() ?: 0

            PieEntry(
                countItems.toFloat(),
                it.name,
                countItems.toString()
            )
        }
    }


    private val _dataImages: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val dataImages: StateFlow<Set<String>> = _dataImages.asStateFlow()

    private val _dataCategory: MutableStateFlow<Results<Category>> =
        MutableStateFlow(Results.Loading(null))
    val dataCategory: StateFlow<Results<Category>> = _dataCategory.asStateFlow()


    fun sendArguments(
        id: String,
        name: String,
        parentIds: Array<String>?,
        imageUrlIds: Array<String>?,
        description: String,
    ) {
        _dataCategory.update {
            Results.Loading(
                Category(
                    id = id,
                    name = name,
                    allParentIds = parentIds?.toList() ?: emptyList(),
                    imagesUrls = imageUrlIds?.toList() ?: emptyList(),
                    description = description,
                )
            )
        }
        fetch(id)
    }

    var currentUser: User? = null
        private set

    private fun fetchUsersNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserUseCase()?.let { user ->
                currentUser = user
                val res = resultWrapper(
                    userRepository.getUsers(user.usersInNetwork)
                ) {
                    _data.update { viewData ->
                        viewData.copy(chartData = mapUsersToChart(it))
                    }
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
            val categoryResults = categoryRepository.getCategory(id)
            val barcodes = (categoryResults as? Results.Success)
                ?.data?.barCodes?.toSet() ?: emptySet()
            _data.update { it.copy(codes = barcodes) }
            _dataCategory.update { categoryResults }
            getLastChanged(id)
            fetchUsersNetwork()
        }
    }

    fun handleShowedDeleted() {
        _data.update { it.copy(isDeleted = Results.Loading(null)) }
    }

    private fun getLastChanged(categoryId: String) {
        safeRunJobWithLoading(Dispatchers.IO) {
            val res2 = historyRepository.getLastAction(
                id = categoryId,
                element = HistoryRepository.ActionElement.CATEGORY
            )
            log.d("last action: $res2")
            val res: Results<LastChangedUi> = resultWrapper(
                res2
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
        }
    }

    fun deleteItem() {
        viewModelScope.launch(Dispatchers.IO) {
            (_dataCategory.value as? Results.Success)?.let { success ->
                val res = categoryRepository.deleteCategoryAndChildren(
                    currentUserUseCase()!!.id,
                    success.data.id
                )
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


    fun setRules(
        userId: String,
        canRead: Boolean,
        canEdit: Boolean,
        canShareRead: Boolean,
        canShareEdit: Boolean
    ) {
        val element = (_dataCategory.value as? Results.Success)?.data ?: return
        val currentUserId = currentUser?.id ?: return

        viewModelScope.launch(Dispatchers.IO) {
            categoryRepository.changeRules(
                elementId = element.id,
                userId = userId,
                grantUserId = currentUserId,
                canRead = canRead,
                canEdit = canEdit,
                canShareRead = canShareRead,
                canShareEdit = canShareEdit
            )
        }
    }

    fun loadUserPermission(
        userId: String,
        handleOnLoad: (Results<UserPermission?>, String) -> Unit
    ) {
        val elementId = (_dataCategory.value as? Results.Success)?.data?.id ?: return
        viewModelScope.launch {
            handleOnLoad(
                permissionRepository.getPermissionByUser(
                    type = Data.Type.CATEGORY,
                    elementId = elementId,
                    userId = userId
                ),
                elementId
            )
        }
    }

}