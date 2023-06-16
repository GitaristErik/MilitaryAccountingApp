    package com.example.militaryaccountingapp.domain.usecase

    import com.example.militaryaccountingapp.data.helper.ResultHelper
    import com.example.militaryaccountingapp.domain.entity.data.Action
    import com.example.militaryaccountingapp.domain.entity.data.ActionType
    import com.example.militaryaccountingapp.domain.entity.data.Data
    import com.example.militaryaccountingapp.domain.entity.user.User
    import com.example.militaryaccountingapp.domain.helper.Results
    import com.example.militaryaccountingapp.domain.repository.CategoryRepository
    import com.example.militaryaccountingapp.domain.repository.HistoryRepository
    import com.example.militaryaccountingapp.domain.repository.ItemRepository
    import com.example.militaryaccountingapp.domain.repository.UserRepository
    import timber.log.Timber

    open class GetHistoryUseCase(
        private val repository: HistoryRepository,
        private val userRepository: UserRepository,
        private val categoryRepository: CategoryRepository,
        private val itemRepository: ItemRepository,
    ) {
        open suspend operator fun invoke(
            limit: Long = 100,
            filters: Set<ActionType> = emptySet(),
    //        page: Int = 0,
        ): Results<List<Triple<Action, Data?, User>>> = ResultHelper.safetyResultWrapper({
            Timber.d("get history with limit = $limit, filters = $filters")
            repository.getHistory(
    //        page = page,
                filters = filters,
                limit = limit,
            )
        }) { list ->
            val res = list.map {
                val userRes = userRepository.getUser(it.userId)
                Timber.d("userRes: $userRes")
                val user = userRes as Results.Success
                val dataRes = if (!it.categoryId.isNullOrEmpty()) {
                    categoryRepository.getCategory(it.categoryId)
                } else if (!it.itemId.isNullOrEmpty()) {
                    itemRepository.getItem(it.itemId)
                } else null
                Timber.d("dataRes: $dataRes")
                Triple(it, (dataRes as? Results.Success)?.data, user.data)
            }
            Results.Success(res)
        }
    }