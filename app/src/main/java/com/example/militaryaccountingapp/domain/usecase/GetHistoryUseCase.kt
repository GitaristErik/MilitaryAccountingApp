package com.example.militaryaccountingapp.domain.usecase

import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.repository.DataRepository

open class GetHistoryUseCase(
    private val repository: DataRepository,
) {
    open suspend operator fun invoke(
        limit: Int = -1,
        filters: Set<ActionType> = emptySet(),
        page: Int = 0,
    ) = repository.getHistory(
        page = page,
        limit = limit,
        filters = filters
    )
}