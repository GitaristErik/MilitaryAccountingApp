package com.example.militaryaccountingapp.presenter.fragment.filter

import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.example.militaryaccountingapp.presenter.model.filter.UserFilterUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.ApiConstant
import com.example.militaryaccountingapp.presenter.utils.ui.TreeNodeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getCurrentUserUseCase: CurrentUserUseCase,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val permissionRepository: PermissionRepository
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val usersUi: List<UserFilterUi> = emptyList(),
        val selectedUsersId: Set<String> = emptySet(),
        val selectedCategoriesIds: Set<String> = emptySet(),
        val selectedItemsIds: Set<String> = emptySet(),
        val isFiltersSelected: Boolean = false,
    )

    private val _dataNodes = MutableStateFlow<List<TreeNodeItem>>(emptyList())
    val dataNodes: StateFlow<List<TreeNodeItem>> = _dataNodes.asStateFlow()


    init {
        Timber.d("init")
        load()
    }

    private var currentUser: User? = null
    private fun load() {
        log.d("fetch")
        safeRunJob(Dispatchers.IO) {
            currentUser = getCurrentUserUseCase()
            loadUsers()
        }
    }

    // Runner
    private var nodesJob: Job? = null
    private val cache = mutableMapOf<String, TreeNodeItem>()
    private fun loadNodes(selectedUsersIds: List<String>) {
        stopRunningJob(nodesJob)
        nodesJob = viewModelScope.launch(Dispatchers.IO) {
            delay(ApiConstant.DELAY_NODES_LOAD)
            val allCategoriesIds = mutableListOf<String>()
            val allItemsIds = mutableListOf<String>()

            selectedUsersIds.forEach { grantUser ->
                (permissionRepository.getPermissionsIdsByUsers(
                    destinationUserId =
                    currentUser!!.id,
                    grantUserId =
                    grantUser,
                    type = Data.Type.CATEGORY
                ) as? Results.Success)?.data?.let { categoriesIds ->

                    allCategoriesIds.addAll(categoriesIds)

                    (permissionRepository.getPermissionsIdsByUsers(
                        destinationUserId =
                        currentUser!!.id,
                        grantUserId =
                        grantUser,
                        type = Data.Type.ITEM
                    ) as? Results.Success)?.data?.let { itemsIds ->

                        allItemsIds.addAll(itemsIds)

                    } ?: log.e("loadNodesHandler error load items")
                } ?: log.e("loadNodesHandler error load categories")
            }

            /*

                        (permissionRepository.getPermissionsIdsByUsers(
                            destinationUserId = currentUser!!.id,
                            grantUserId = currentUser!!.id,
                            type = Data.Type.CATEGORY
                        ) as? Results.Success)?.data?.let { categoriesIds ->
                            allCategoriesIds.addAll(categoriesIds)
                        } ?: log.e("loadNodesHandler error load self categories")

                        (permissionRepository.getPermissionsIdsByUsers(
                            destinationUserId = currentUser!!.id,
                            grantUserId = currentUser!!.id,
                            type = Data.Type.ITEM
                        ) as? Results.Success)?.data?.let { categoriesIds ->
                            allItemsIds.addAll(categoriesIds)
                        } ?: log.e("loadNodesHandler error load self items")
            */


            val categoriesRes = categoryRepository.getCategories(allCategoriesIds)
            val itemsRes = itemRepository.getItems(allItemsIds)
            log.d("loadNodesHandler categoriesRes=$categoriesRes itemsRes=$itemsRes")
            TreeNodeHelper.loadNodesHandler(categoriesRes, itemsRes, cache) { nodes ->
                log.d("loadNodesHandler update dataNodes $nodes")
                _dataNodes.update { nodes }
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            (userRepository.getUsers(currentUser!!.usersInNetwork) as? Results.Success)?.data?.let { oldUsers ->
                val users = mapUsersToUi(oldUsers + currentUser!!)
                val usersIds = users.map { it.id }
                _data.update { viewData ->
                    viewData.copy(
                        usersUi = users,
                        selectedUsersId = usersIds.toSet()
                    )
                }
                loadNodes(usersIds)
            }
        }
    }

    private suspend fun mapUsersToUi(users: List<User>): List<UserFilterUi> {
        return users.map {
            val countItems = (permissionRepository.getReadCount(
                grantUserId = it.id,
                destinationUserId = currentUser!!.id
            ) as? Results.Success)?.data?.toInt() ?: 0

            UserFilterUi(
                id = it.id,
                name = it.name,
                imageUrl = it.imageUrl,
                count = countItems,
            )
        }
    }

    fun changeUserSelection(userId: String, checked: Boolean = false) {
        val usersUi = _data.value.usersUi.map { user ->
            if (user.id == userId)
                user.copy(checked = checked)
            else
                user
        }

        val selectedUsersId: Set<String> = _data.value.selectedUsersId.let {
            if (checked) {
                it.plus(userId)
            } else {
                it.minus(userId)
            }
        }

        _data.update {
            it.copy(
                usersUi = usersUi,
                selectedItemsIds = emptySet(),
                selectedCategoriesIds = emptySet(),
                selectedUsersId = selectedUsersId,
                isFiltersSelected = false,
            )
        }

        loadNodes(selectedUsersId.toList())
    }

    fun changeItemSelection(id: String, checked: Boolean = false) {
        val itemsId: Set<String> = with(_data.value.selectedItemsIds) {
            if (checked) {
                this.plus(id)
            } else {
                this.minus(id)
            }
        }
        val isFiltersSelected =
            _data.value.selectedCategoriesIds.isNotEmpty() || itemsId.isNotEmpty()

        _data.update {
            it.copy(
                selectedItemsIds = itemsId,
                isFiltersSelected = isFiltersSelected,
            )
        }
    }

    /**
     * Change state category and children
     * @param id id category
     * @param children list of children. Pair<id, isItem>. isItem - true if item, false if category
     * @param checked state category
     */
    fun changeCategorySelection(
        id: String,
        children: List<Pair<String, Boolean>>,
        checked: Boolean = false
    ) {
        val categoriesId = with(_data.value.selectedCategoriesIds) {
            if (checked) {
                this + id
            } else {
                this - id
            }
        }.toMutableList().apply {
            removeAll(children
                .filter { !it.second }
                .map { it.first }
            )
        }

        val itemsId = _data.value.selectedItemsIds.toMutableList().apply {
            removeAll(children
                .filter { it.second }
                .map { it.first }
            )
        }

        val isFiltersSelected = categoriesId.isNotEmpty() || itemsId.isNotEmpty()

        _data.update {
            it.copy(
                selectedItemsIds = itemsId.toSet(),
                selectedCategoriesIds = categoriesId.toSet(),
                isFiltersSelected = isFiltersSelected,
            )
        }
    }

}
