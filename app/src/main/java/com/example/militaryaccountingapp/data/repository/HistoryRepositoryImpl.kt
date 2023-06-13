package com.example.militaryaccountingapp.data.repository

import com.example.militaryaccountingapp.data.helper.ResultHelper.safetyResultWrapper
import com.example.militaryaccountingapp.domain.entity.data.Action
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository.ActionElement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor() : HistoryRepository {

    companion object {
        private const val ACTIONS = "actions"
    }

    private val firestoreInstance: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val collection = firestoreInstance.collection(ACTIONS)

    override suspend fun getLastAction(
        id: String,
        filters: Set<ActionType>,
        element: ActionElement
    ): Results<Action> = safetyResultWrapper({
        collection
            .let { query ->
                when (element) {
                    ActionElement.ITEM -> query.whereEqualTo("itemId", id)
                    ActionElement.CATEGORY -> query.whereEqualTo("categoryId", id)
                }
            }.let {
                if (filters.isNotEmpty()) {
                    it.whereIn("action", filters.toList())
                } else {
                    it
                }
            }.orderBy("timestamp", Direction.DESCENDING)
            .limit(1)
            .get().await()
    }) {
        Results.Success(it.toObjects(Action::class.java).first())
    }

}