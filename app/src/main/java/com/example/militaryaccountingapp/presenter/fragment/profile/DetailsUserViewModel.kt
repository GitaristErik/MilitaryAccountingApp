package com.example.militaryaccountingapp.presenter.fragment.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.helper.Results.Companion.anyData
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.filter.FilterViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.DetailsUserViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.filter.TreeNodeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailsUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserUseCase: CurrentUserUseCase,
    private val permissionRepository: PermissionRepository,
    private val itemRepository: ItemRepository,
    private val categoryRepository: CategoryRepository
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val userProfileUri: Uri? = null,
        val user: Results<User> = Results.Loading(null),
        val inNetwork: Results<Boolean> = Results.Loading(null),
    )

    private val _dataNodes = MutableStateFlow<List<TreeNodeItem>>(emptyList())
    val dataNodes: MutableStateFlow<List<TreeNodeItem>> = _dataNodes


    // Runner
    private var nodesJob: Job? = null
    private val cache = mutableMapOf<String, TreeNodeItem>()
    private fun loadNodes(currentUser: String, allUsers: List<String>) {
        stopRunningJob(nodesJob)
        nodesJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300)
            val res = permissionRepository.getPermissionsByUsers(
                currentUser, allUsers
            )
            (res as? Results.Success)?.data?.let { (categoriesIds, itemsIds) ->
                val categories =
                    (categoryRepository.getCategories(categoriesIds) as Results.Success).data
                val items = (itemRepository.getItems(itemsIds) as Results.Success).data
                if ((data.value.inNetwork as? Results.Success)?.data == true) {
                    val nodes = FilterViewModel.findRootCategories(categories).map { category ->
                        FilterViewModel.convertCategoryToTreeNodeItem(
                            category, categories + items, cache = cache
                        )
                    }
                    log.d("loadNodes | nodes: $nodes")
                    _dataNodes.update { nodes }
                }
            }
        }
    }


    init {
        Timber.d("init")
    }

    override fun onCleared() {
        super.onCleared()
        log.e("onCleared")
    }

    fun sendTempUser(userid: String, name: String?, fullName: String?, rank: String?) {
        _data.update {
            it.copy(
                user = Results.Loading(
                    User(
                        id = userid,
                        name = name ?: "",
                        fullName = fullName ?: "",
                        rank = rank ?: "",
                    )
                )
            )
        }

        safeRunJobWithLoading(Dispatchers.IO) {
            val res = resultWrapper(
                userRepository.getUser(userid)
            ) {
                log.d("get user SUCCESS: $it")
                loadInNetwork(it.id)
                Results.Success(it)
            }
            _data.update { it.copy(user = res) }
        }
    }

    private fun loadInNetwork(selectedUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            log.d("loadInNetwork | start")
            currentUserUseCase()?.let { currentUser ->
                log.d("loadInNetwork | current user: $currentUser")
                currentUser.usersInNetwork.let { usersInNetwork ->
                    log.d("loadInNetwork | users in network cUser: $usersInNetwork")
                    _data.update {
                        it.copy(
                            inNetwork = Results.Success(
                                usersInNetwork.contains(selectedUserId)
                            )
                        )
                    }
                    loadNodes(currentUser.id, usersInNetwork)
                }
            } ?: log.d("loadInNetwork | current user is null")
        }
    }

    fun addMember() {
        executeMemberAction(isAdd = true)
    }

    fun deleteMember() {
        executeMemberAction(isAdd = false)
    }

    private fun executeMemberAction(isAdd: Boolean = true) {
        (data.value.user as? Results.Success)?.data?.let { user ->
            viewModelScope.launch(Dispatchers.IO) {
                currentUserUseCase()?.let { cUser ->
                    log.d("executeMemberAction | user: $user | cUser: $cUser | isAdd: $isAdd")
                    val res = userRepository.updateCurrentUserInfo(
                        cUser.id, mapOf(
                            "usersInNetwork" to if (isAdd) {
                                cUser.usersInNetwork.toSet().plus(user.id).toList()
                            } else {
                                cUser.usersInNetwork.toSet().minus(user.id).toList()
                            }
                        )
                    )
                    log.d("executeMemberAction | res: $res")
                    if (res is Results.Success) {
                        _toast.value = if (isAdd) "User add to your network!"
                        else "User remove from your network!"
                        loadInNetwork(
                            data.value.user.anyData()?.id ?: return@launch
                        )
                    } else {
                        log.e("Error update user info: $res")
                    }
                }
            }
        }
    }
}