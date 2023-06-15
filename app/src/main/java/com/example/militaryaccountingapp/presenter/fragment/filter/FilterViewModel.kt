package com.example.militaryaccountingapp.presenter.fragment.filter

import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.example.militaryaccountingapp.presenter.model.filter.UserFilterUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.FilterDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getCurrentUserUseCase: CurrentUserUseCase,
    private val historyRepository: HistoryRepository,
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
        val filterDate: FilterDate = FilterDate.PickDay(Date().time),
        val isFiltersSelected: Boolean = false,
    )

    private val _dataNodes = MutableStateFlow<List<TreeNodeItem>>(emptyList())
    val dataNodes: MutableStateFlow<List<TreeNodeItem>> = _dataNodes

    val filterDate: FilterDate get() = _data.value.filterDate

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
            loadNodes()
        }
    }

    // Runner
    private var nodesJob: Job? = null
    private val cache = mutableMapOf<String, TreeNodeItem>()
    private fun loadNodes() {
        stopRunningJob(nodesJob)
        nodesJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300)
            val res = permissionRepository.getPermissionsByUsers(
                currentUser!!.id,
                data.value.selectedUsersId.toList()
            )
            (res as? Results.Success)?.data?.let { (categoriesIds, itemsIds) ->
                val categories =
                    (categoryRepository.getCategories(categoriesIds) as Results.Success).data
                val items = (itemRepository.getItems(itemsIds) as Results.Success).data
                val nodes = findRootCategories(categories).map { category ->
                    convertCategoryToTreeNodeItem(category, categories + items, cache = cache)
                }
                log.d("loadNodes | nodes: $nodes")
                _dataNodes.update { nodes }
            }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            (userRepository.getUsers(currentUser!!.usersInNetwork) as? Results.Success)?.data?.let { oldUsers ->
                val users = mapUsersToUi(oldUsers)
                _data.update {
                    it.copy(
                        usersUi = users,
                        selectedUsersId = currentUser!!.usersInNetwork.toSet()
                    )
                }
            }
        }
    }

    private suspend fun mapUsersToUi(users: List<User>): List<UserFilterUi> {
        return users.map {
            val countItems = (permissionRepository.getReadCount(it.id)
                    as? Results.Success)?.data?.toInt() ?: 0

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

        loadNodes()
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

    fun changeDateSelection(date: FilterDate) {
        _data.update {
            it.copy(
                filterDate = date,
                isFiltersSelected = with(_data.value) {
                    selectedCategoriesIds.isNotEmpty() || selectedItemsIds.isNotEmpty()
                }
            )
        }
    }


    companion object {
        fun findRootCategories(categories: List<Category>): List<Category> {
            val categoryIndex = categories.associateBy { it.id }
            val rootCategories = mutableListOf<Category>()

            for (category in categories) {
                // Перевіряємо, чи відсутнє поле parentId у списку всіх категорій
                val isRootCategory = category.parentId !in categoryIndex
                if (isRootCategory) {
                    rootCategories.add(category)
                }
            }

            return rootCategories
        }

        private fun convertCategoriesToTreeNodeItems(categories: List<Category>): List<TreeNodeItem> {
//        currentUser.rootCategories
            val rootCategories = mutableListOf<TreeNodeItem>()

            // Перетворюємо кожну категорію в TreeNodeItem
            for (category in categories) {
                if (category.parentId == null) {
                    val treeNodeItem = convertCategoryToTreeNodeItem(category, categories)
                    rootCategories.add(treeNodeItem)
                }
            }

            return rootCategories
        }

        private fun convertCategoryToTreeNodeItem(
            category: Data,
            elements: List<Data>
        ): TreeNodeItem {
            val childItems = mutableListOf<TreeNodeItem>()

            // Перетворюємо дочірні категорії в TreeNodeItem
            for (childCategory in elements) {
                if (childCategory.parentId == category.id) {
                    val childTreeNodeItem = convertCategoryToTreeNodeItem(childCategory, elements)
                    childItems.add(childTreeNodeItem)
                }
            }

            return TreeNodeItem(
                nodeViewId = category.id,
                child = childItems,
                name = category.name,
                id = category.id,
                isCategory = true
            )
        }


        fun convertCategoryToTreeNodeItem(
            rootElement: Data,
            elements: List<Data>,
            cache: MutableMap<String, TreeNodeItem>
        ): TreeNodeItem {
            // Перевіряємо, чи результат вже є в кеші
            if (rootElement.id in cache) {
                return cache[rootElement.id]!!
            }

            val childItems = mutableListOf<TreeNodeItem>()

            // Перетворюємо дочірні категорії в TreeNodeItem
            for (childCategory in elements) {
                if (childCategory.parentId == rootElement.id) {
                    val childTreeNodeItem =
                        convertCategoryToTreeNodeItem(childCategory, elements, cache)
                    childItems.add(childTreeNodeItem)
                }
            }

            val treeNodeItem = TreeNodeItem(
                nodeViewId = rootElement.id,
                child = childItems,
                name = rootElement.name,
                id = rootElement.id,
                isCategory = true
            )

            // Зберігаємо результат у кеші
            cache[rootElement.id] = treeNodeItem

            return treeNodeItem
        }

    }
}
