package com.example.militaryaccountingapp.presenter.fragment.filter

import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import com.example.militaryaccountingapp.presenter.model.filter.UserFilterUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FilterViewModel @Inject constructor() : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val usersUi: List<UserFilterUi> = emptyList(),
        val selectedUsersId: List<Int> = emptyList()
    )

    private val selectedCategoriesIds: MutableList<Int> = mutableListOf()
    private val selectedItemsIds: MutableList<Int> = mutableListOf()

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


    fun changeUserSelection(userId: Int, checked: Boolean = false) {
        log.d("changeUserSelection: userId=$userId, checked=$checked")
        _data.update {
            it.copy(
                usersUi = it.usersUi.map { user ->
                    if (user.id == userId)
                        user.copy(checked = checked)
                    else
                        user
                },
                selectedUsersId = if (checked)
                    it.selectedUsersId + userId
                else
                    it.selectedUsersId - userId
            )
        }
        _dataNodes.update { getTempNodes() }
    }

    fun changeItemSelection(id: Int, checked: Boolean = false) {
        if (checked) {
            selectedItemsIds.add(id)
        } else {
            selectedItemsIds.remove(id)
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
        if (checked) {
            selectedCategoriesIds.add(id)
        } else {
            selectedCategoriesIds.remove(id)
        }
        selectedItemsIds.removeAll(children
            .filter { it.second }
            .map { it.first }
        )
        selectedCategoriesIds.removeAll(children
            .filter { !it.second }
            .map { it.first }
        )
    }

}