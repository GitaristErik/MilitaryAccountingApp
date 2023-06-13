package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SimpleEndlessRecyclerScrollListener(
    private val offset: Int = 0,
    private val stopScrollPotentialEnergy: Boolean = false,
    private val onReachEnd: () -> Unit,
) : RecyclerView.OnScrollListener() {
    var isEnabled = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val manager = recyclerView.layoutManager as? LinearLayoutManager
        if (isEnabled && manager != null) {
            val visibleItemCount = manager.childCount
            val totalItemCount = manager.itemCount
            val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()

            if ((firstVisibleItemPosition + visibleItemCount + offset >= totalItemCount)) {
                onReachEnd()
                if (stopScrollPotentialEnergy &&
                    manager.findLastVisibleItemPosition() == totalItemCount - 1
                ) {
                    recyclerView.stopScroll()
                }
            }
        }
    }
}