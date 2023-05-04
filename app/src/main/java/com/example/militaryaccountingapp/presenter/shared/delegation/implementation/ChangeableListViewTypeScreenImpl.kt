package com.example.militaryaccountingapp.presenter.shared.delegation.implementation

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.presenter.shared.delegation.ChangeableListViewTypeScreen
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType

class ChangeableListViewTypeScreenImpl : ChangeableListViewTypeScreen {

    private lateinit var gridLayoutManager: GridLayoutManager
    private var spanCount: Int = 2

    private var currentViewType: ViewType = ViewType.GRID

    override fun initializeViewTypeScreen(
        context: Context,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        viewType: ViewType,
        topFullSpanItemCount: () -> Int,
        singleSpanItemCount: () -> Int,
    ) {
        currentViewType = viewType
        spanCount = context.resources.getInteger(R.integer.grid_span)
        gridLayoutManager = GridLayoutManager(context, spanCount).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val topItemCount = topFullSpanItemCount()
                    val itemCountWithoutLoadingBottom = topItemCount + singleSpanItemCount()
                    val canTakeAllSpan =
                        position < topItemCount || position >= itemCountWithoutLoadingBottom
                    return if (canTakeAllSpan) {
                        spanCount
                    } else {
                        1
                    }
                }
            }
        }
        updateLayoutManager(viewType, recyclerView, adapter)
    }

    override fun onHandleLayoutType(
        viewType: ViewType,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
        onViewTypeChanged: () -> Unit,
    ) {
        val isViewTypeChanged = currentViewType != viewType
        currentViewType = viewType
        if (isViewTypeChanged) {
            onViewTypeChanged()
            updateLayoutManager(viewType, recyclerView, adapter)
        }
    }

    override fun currentSpanSize(): Int = spanCount

    private fun updateLayoutManager(
        viewType: ViewType,
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<*>,
    ) {
        recyclerView.layoutManager = gridLayoutManager
        if (viewType == ViewType.GRID) {
            gridLayoutManager.spanCount = spanCount
            recyclerView.adapter = adapter
        } else {
            gridLayoutManager.spanCount = 1
            recyclerView.adapter = adapter
        }
    }
}