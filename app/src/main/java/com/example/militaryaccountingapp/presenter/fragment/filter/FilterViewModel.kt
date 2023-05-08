package com.example.militaryaccountingapp.presenter.fragment.filter

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.example.militaryaccountingapp.presenter.model.filter.UserFilterUi
import com.example.militaryaccountingapp.presenter.utils.common.constant.FilterDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FilterViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val usersUi: List<UserFilterUi> = emptyList(),
        val selectedUsersId: List<Int> = emptyList(),
        val selectedCategoriesIds: List<Int> = emptyList(),
        val selectedItemsIds: List<Int> = emptyList(),
        val filterDate: FilterDate = FilterDate.PickDay(Date().time),
        val isFiltersSelected: Boolean = false,
    )

    private val _dataNodes = MutableStateFlow<List<TreeNodeItem>>(emptyList())
    val dataNodes: MutableStateFlow<List<TreeNodeItem>> = _dataNodes

    init {
        Timber.d("init")
        fetch()
    }

    private fun fetch() {
        log.d("fetch")

        val users = getTempUsers()
        val selectedUsersId = MutableList(3) { it }
        val nodes = getTempNodes()

        _data.update {
            it.copy(
                usersUi = users,
                selectedUsersId = selectedUsersId,
            )
        }

        _dataNodes.update { nodes }
    }

    fun changeUserSelection(userId: Int, checked: Boolean = false) {
        val usersUi = _data.value.usersUi.map { user ->
            if (user.id == userId)
                user.copy(checked = checked)
            else
                user
        }

        val selectedUsersId = _data.value.selectedUsersId.let {
            if (checked) {
                it + userId
            } else {
                it - userId
            }
        }
        val isFiltersSelected = (selectedUsersId.isNotEmpty() &&
                (_data.value.selectedCategoriesIds.isNotEmpty() || _data.value.selectedItemsIds.isNotEmpty())
                && _data.value.filterDate.displayName.isNotEmpty())

        _data.update {
            it.copy(
                usersUi = usersUi,
                selectedUsersId = selectedUsersId,
                isFiltersSelected = isFiltersSelected,
            )
        }

        _dataNodes.update { getTempNodes() }
    }

    fun changeItemSelection(id: Int, checked: Boolean = false) {
        val itemsId = with(_data.value.selectedItemsIds) {
            if (checked) {
                this + id
            } else {
                this - id
            }
        }
        val isFiltersSelected = (_data.value.selectedUsersId.isNotEmpty() &&
                (_data.value.selectedCategoriesIds.isNotEmpty() || itemsId.isNotEmpty()) &&
                _data.value.filterDate.displayName.isNotEmpty())

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
        id: Int,
        children: List<Pair<Int, Boolean>>,
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

        val isFiltersSelected = (_data.value.selectedUsersId.isNotEmpty() &&
                (categoriesId.isNotEmpty() || itemsId.isNotEmpty()) &&
                _data.value.filterDate.displayName.isNotEmpty())

        _data.update {
            it.copy(
                selectedItemsIds = itemsId,
                selectedCategoriesIds = categoriesId,
                isFiltersSelected = isFiltersSelected,
            )
        }
    }

    fun changeDateSelection(date: FilterDate) {
        _data.update {
            it.copy(
                filterDate = date,
                isFiltersSelected = with(_data.value) {
                    (selectedUsersId.isNotEmpty() &&
                            (selectedCategoriesIds.isNotEmpty() || selectedItemsIds.isNotEmpty())
                            && filterDate.displayName.isNotEmpty())
                }
            )
        }
    }

    fun changeFiltersSelected(isSelected: Boolean) {
        _data.update {
            it.copy(
                isFiltersSelected = isSelected,
            )
        }
    }

    private companion object {
        private fun getTempUsers(): List<UserFilterUi> = List(3) {
            UserFilterUi(
                id = it,
                name = "Name $it",
                imageUrl = "https://media.npr.org/assets/img/2016/10/26/hacksaw-ridge-hacksawridge_d22-10131_rgb_sq-9241d1a7ee125fdfde8abd5e8585484682c1efbf-s800-c85.jpg",
                count = (Math.random() * 100).toInt(),
                checked = true,
            )
        }

        private fun getTempNodes() = List(1) { generateRandomTreeNodeItem(4) }

        private fun generateRandomTreeNodeItem(maxDepth: Int, parent: Int? = null): TreeNodeItem {
            val id = Random.nextInt()
            val name = "Node $id"
            val checked = Random.nextBoolean()
            val isCategory = Random.nextBoolean()
            val child = if (maxDepth > 0) {
                val childCount = Random.nextInt(4)
                List(childCount) { generateRandomTreeNodeItem(maxDepth - 1, id) }
            } else {
                emptyList()
            }
            return TreeNodeItem(
                id.toString(),
                child,
                name,
                id,
                checked,
                parentCategoryId = parent,
                isCategory,
            )
        }

    }
}
