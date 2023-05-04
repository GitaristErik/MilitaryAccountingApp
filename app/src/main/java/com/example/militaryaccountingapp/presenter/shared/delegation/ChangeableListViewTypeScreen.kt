package com.example.militaryaccountingapp.presenter.shared.delegation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType

interface ChangeableListViewTypeScreen {
    fun initializeViewTypeScreen(
        context: Context,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        viewType: ViewType,
        topFullSpanItemCount: () -> Int,
        singleSpanItemCount: () -> Int,
    )

    fun currentSpanSize(): Int

    fun onHandleLayoutType(
        viewType: ViewType,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        onViewTypeChanged: () -> Unit,
    )
}