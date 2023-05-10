package com.example.militaryaccountingapp.presenter.model.filter

import android.os.Bundle
import com.gg.gapo.treeviewlib.model.NodeData

data class TreeNodeItem(
    override val nodeViewId: String,
    val child: List<TreeNodeItem>,
    val name: String,
    val id: Int,
    val checked: Boolean = false,
    val parentCategoryId: Int? = null,
    val isCategory: Boolean = false,
) : NodeData<TreeNodeItem> {
    override fun getNodeChild(): List<TreeNodeItem> = child

    override fun areItemsTheSame(item: NodeData<TreeNodeItem>): Boolean {
        return if (item !is TreeNodeItem) false
        else nodeViewId == item.nodeViewId
    }

    override fun areContentsTheSame(item: NodeData<TreeNodeItem>): Boolean {
        return if (item !is TreeNodeItem) false
        else item.name == name && item.child.size == child.size
    }

    override fun getChangePayload(item: NodeData<TreeNodeItem>): Bundle {
        return Bundle()
    }
}