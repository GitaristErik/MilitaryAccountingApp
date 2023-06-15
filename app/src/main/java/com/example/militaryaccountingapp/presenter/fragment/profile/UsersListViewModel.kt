package com.example.militaryaccountingapp.presenter.fragment.profile

import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.UserRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.profile.UsersListViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.UserSearchUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val currentUserUseCase: CurrentUserUseCase,
    private val userRepository: UserRepository
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        var text: String? = null,
        var mainData: Results<List<UserSearchUi>> = Results.Loading(),
    )


    fun setText(text: String) {
        if (data.value.text == text) return
        _data.update { it.copy(text = text) }
        reload()
    }

    fun reload() {
//        _data.update { it.copy(page = 1L, mainData = Results.Loading()) }
        fetch()
    }

    private fun fetch() {
        log.d("fetch searched users with text: ${data.value.text}")
        val cleanText = data.value.text?.trim() ?: return
        safeRunJob(Dispatchers.Default) {
            val res = resultWrapper(
                userRepository.searchUsers(cleanText)
            ) {
                log.d("searched users SUCCESS: $it")
                Results.Success(mapToUserUi(it))
            }
            _data.update { it.copy(mainData = res) }
        }
    }

    private fun mapToUserUi(users: List<User>) = users.map {
        UserSearchUi(
            id = it.id,
            name = it.name,
            imageUrl = it.imageUrl,
            fullName = it.fullName,
            rank = it.rank,
        )
    }

}